package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.RecordVisitRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateHomeVisitRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.HomeVisitResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.HomeVisit;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.HomeVisitRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeVisitService {

    private final HomeVisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;
    private final AuditLogService auditLogService;
    private final AlertService alertService;

    @Transactional
    public HomeVisitResponse recordVisit(RecordVisitRequest req) {
        if (req.getClientRequestId() != null) {
            Optional<HomeVisit> existing = visitRepository.findByClientRequestId(req.getClientRequestId());
            if (existing.isPresent()) {
                return toResponse(existing.get()); // retried offline-queue flush — safe no-op
            }
        }

        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + req.getPatientId()));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }

        boolean anyStructuredSymptom =
                tf(req.getSymptomCoughGe2w()) || tf(req.getSymptomFever()) ||
                tf(req.getSymptomNightSweats()) || tf(req.getSymptomWeightLoss()) ||
                tf(req.getSymptomHemoptysis()) || tf(req.getSideEffectNeuropathy()) ||
                tf(req.getSideEffectJaundice()) || tf(req.getSideEffectNausea()) ||
                tf(req.getSideEffectRash()) || tf(req.getSideEffectDizziness());
        boolean hasObservation =
                anyStructuredSymptom ||
                (req.getPillCountRecorded()   != null) ||
                (req.getAdverseEventGrade()   != null) ||
                (req.getSymptomsReported()    != null && !req.getSymptomsReported().isBlank()) ||
                (req.getSideEffectsReported() != null && !req.getSideEffectsReported().isBlank()) ||
                (req.getPsychosocialNotes()   != null && !req.getPsychosocialNotes().isBlank());
        if (!hasObservation) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must record at least one clinical observation before saving the visit.");
        }

        boolean discrepancy = false;
        if (req.getPillCountRecorded() != null && req.getPillCountExpected() != null) {
            discrepancy = !req.getPillCountRecorded().equals(req.getPillCountExpected());
        }

        HomeVisit visit = HomeVisit.builder()
                .patient(patient)
                .chw(chw)
                .visitDate(req.getVisitDate())
                .visitStatus("ATTENDED_TO")
                .adherenceStatus(req.getAdherenceStatus())
                .pillCountRecorded(req.getPillCountRecorded())
                .pillCountExpected(req.getPillCountExpected())
                .pillCountDiscrepancy(discrepancy)
                .symptomCoughGe2w(tf(req.getSymptomCoughGe2w()))
                .symptomFever(tf(req.getSymptomFever()))
                .symptomNightSweats(tf(req.getSymptomNightSweats()))
                .symptomWeightLoss(tf(req.getSymptomWeightLoss()))
                .symptomHemoptysis(tf(req.getSymptomHemoptysis()))
                .sideEffectNeuropathy(tf(req.getSideEffectNeuropathy()))
                .sideEffectJaundice(tf(req.getSideEffectJaundice()))
                .sideEffectNausea(tf(req.getSideEffectNausea()))
                .sideEffectRash(tf(req.getSideEffectRash()))
                .sideEffectDizziness(tf(req.getSideEffectDizziness()))
                .presumptiveTb(computePresumptiveTb(req.getSymptomCoughGe2w(), req.getSymptomFever(),
                        req.getSymptomNightSweats(), req.getSymptomWeightLoss(), req.getSymptomHemoptysis()))
                .symptomsReported(req.getSymptomsReported())
                .sideEffectsReported(req.getSideEffectsReported())
                .psychosocialNotes(req.getPsychosocialNotes())
                .nextVisitDate(req.getNextVisitDate())
                .adverseEventGrade(req.getAdverseEventGrade())
                .referralInitiated(req.getReferralInitiated() != null ? req.getReferralInitiated() : false)
                .clientRequestId(req.getClientRequestId())
                .syncStatus(SyncStatus.PENDING)
                .build();

        visitRepository.save(visit);
        auditLogService.log("RECORD_VISIT", "home_visits", visit.getId());

        if (req.getAdverseEventGrade() != null && req.getAdverseEventGrade() >= 3) {
            alertService.createAdverseEventAlert(patient, chw, visit);
        }

        return toResponse(visit);
    }

    /**
     * Corrects an already-submitted visit. recordVersion must match the row's
     * current value — a mismatch means someone else updated it since the
     * caller last read it, so we reject with 409 rather than silently
     * overwriting their change.
     */
    @Transactional
    public HomeVisitResponse updateVisit(UUID visitId, UpdateHomeVisitRequest req) {
        Chw chw = resolveCurrentChw();
        HomeVisit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found: " + visitId));
        if (!visit.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: visit not recorded by you");
        }
        if (!visit.getRecordVersion().equals(req.getRecordVersion())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This visit was changed since you last loaded it (expected version "
                            + visit.getRecordVersion() + ", got " + req.getRecordVersion() + "). Reload and retry.");
        }

        boolean discrepancy = visit.getPillCountDiscrepancy();
        if (req.getPillCountRecorded() != null && req.getPillCountExpected() != null) {
            discrepancy = !req.getPillCountRecorded().equals(req.getPillCountExpected());
        }

        Integer previousGrade = visit.getAdverseEventGrade();
        visit.setAdherenceStatus(req.getAdherenceStatus());
        visit.setPillCountRecorded(req.getPillCountRecorded());
        visit.setPillCountExpected(req.getPillCountExpected());
        visit.setPillCountDiscrepancy(discrepancy);
        // Structured symptoms: preserve existing values when the request omits them (null),
        // so an edit that only touches notes/adherence can't silently wipe the symptom screen.
        if (req.getSymptomCoughGe2w()      != null) visit.setSymptomCoughGe2w(req.getSymptomCoughGe2w());
        if (req.getSymptomFever()          != null) visit.setSymptomFever(req.getSymptomFever());
        if (req.getSymptomNightSweats()    != null) visit.setSymptomNightSweats(req.getSymptomNightSweats());
        if (req.getSymptomWeightLoss()     != null) visit.setSymptomWeightLoss(req.getSymptomWeightLoss());
        if (req.getSymptomHemoptysis()     != null) visit.setSymptomHemoptysis(req.getSymptomHemoptysis());
        if (req.getSideEffectNeuropathy()  != null) visit.setSideEffectNeuropathy(req.getSideEffectNeuropathy());
        if (req.getSideEffectJaundice()    != null) visit.setSideEffectJaundice(req.getSideEffectJaundice());
        if (req.getSideEffectNausea()      != null) visit.setSideEffectNausea(req.getSideEffectNausea());
        if (req.getSideEffectRash()        != null) visit.setSideEffectRash(req.getSideEffectRash());
        if (req.getSideEffectDizziness()   != null) visit.setSideEffectDizziness(req.getSideEffectDizziness());
        // Recompute from the visit's effective (post-merge) TB symptoms.
        visit.setPresumptiveTb(computePresumptiveTb(visit.getSymptomCoughGe2w(), visit.getSymptomFever(),
                visit.getSymptomNightSweats(), visit.getSymptomWeightLoss(), visit.getSymptomHemoptysis()));
        visit.setSymptomsReported(req.getSymptomsReported());
        visit.setSideEffectsReported(req.getSideEffectsReported());
        visit.setPsychosocialNotes(req.getPsychosocialNotes());
        visit.setNextVisitDate(req.getNextVisitDate());
        visit.setAdverseEventGrade(req.getAdverseEventGrade());
        if (req.getReferralInitiated() != null) {
            visit.setReferralInitiated(req.getReferralInitiated());
        }
        visit.setRecordVersion(visit.getRecordVersion() + 1);

        visitRepository.save(visit);
        auditLogService.log("UPDATE_VISIT", "home_visits", visit.getId());

        boolean enteredSevereRange = req.getAdverseEventGrade() != null && req.getAdverseEventGrade() >= 3
                && (previousGrade == null || previousGrade < 3);
        if (enteredSevereRange) {
            alertService.createAdverseEventAlert(visit.getPatient(), chw, visit);
        }

        return toResponse(visit);
    }

    public List<HomeVisitResponse> getVisitsForPatient(UUID patientId) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }
        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<HomeVisitResponse> getVisitsForPatientAdmin(UUID patientId) {
        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<HomeVisitResponse> getLatestForPatient(UUID patientId) {
        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId)
                .stream().findFirst().map(this::toResponse);
    }

    public List<HomeVisitResponse> getVisitsForChw(UUID chwId) {
        return visitRepository.findByChwIdOrderByVisitDateDesc(chwId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public HomeVisitResponse getVisit(UUID visitId) {
        Chw chw = resolveCurrentChw();
        HomeVisit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));
        if (!visit.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: visit not recorded by you");
        }
        return toResponse(visit);
    }

    /** Null-safe truthiness for an optional Boolean flag from the request. */
    private static boolean tf(Boolean b) {
        return b != null && b;
    }

    /**
     * WHO four-symptom TB screen for people living with HIV: a positive screen —
     * any one of current cough, fever, night sweats or weight loss (we also
     * include hemoptysis) — flags the patient as presumptive TB for sputum testing.
     */
    private static boolean computePresumptiveTb(Boolean cough, Boolean fever,
                                                Boolean nightSweats, Boolean weightLoss, Boolean hemoptysis) {
        return tf(cough) || tf(fever) || tf(nightSweats) || tf(weightLoss) || tf(hemoptysis);
    }

    private Chw resolveCurrentChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CHW profile not found for user: " + email));
    }

    private HomeVisitResponse toResponse(HomeVisit v) {
        return HomeVisitResponse.builder()
                .id(v.getId())
                .patientId(v.getPatient().getId())
                .patientName(v.getPatient().getFullName())
                .patientCode(v.getPatient().getPatientCode())
                .chwId(v.getChw().getId())
                .chwName(v.getChw().getUser().getFullName())
                .visitDate(v.getVisitDate())
                .visitStatus(v.getVisitStatus() != null ? v.getVisitStatus() : "ATTENDED_TO")
                .adherenceStatus(v.getAdherenceStatus())
                .pillCountRecorded(v.getPillCountRecorded())
                .pillCountExpected(v.getPillCountExpected())
                .pillCountDiscrepancy(v.getPillCountDiscrepancy())
                .symptomCoughGe2w(v.getSymptomCoughGe2w())
                .symptomFever(v.getSymptomFever())
                .symptomNightSweats(v.getSymptomNightSweats())
                .symptomWeightLoss(v.getSymptomWeightLoss())
                .symptomHemoptysis(v.getSymptomHemoptysis())
                .sideEffectNeuropathy(v.getSideEffectNeuropathy())
                .sideEffectJaundice(v.getSideEffectJaundice())
                .sideEffectNausea(v.getSideEffectNausea())
                .sideEffectRash(v.getSideEffectRash())
                .sideEffectDizziness(v.getSideEffectDizziness())
                .presumptiveTb(v.getPresumptiveTb())
                .symptomsReported(v.getSymptomsReported())
                .sideEffectsReported(v.getSideEffectsReported())
                .psychosocialNotes(v.getPsychosocialNotes())
                .nextVisitDate(v.getNextVisitDate())
                .adverseEventGrade(v.getAdverseEventGrade())
                .referralInitiated(v.getReferralInitiated())
                .recordVersion(v.getRecordVersion())
                .syncStatus(v.getSyncStatus().name())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

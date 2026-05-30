package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.RecordVisitRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public HomeVisitResponse recordVisit(RecordVisitRequest req) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + req.getPatientId()));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }

        boolean discrepancy = false;
        if (req.getPillCountRecorded() != null && req.getPillCountExpected() != null) {
            discrepancy = !req.getPillCountRecorded().equals(req.getPillCountExpected());
        }

        HomeVisit visit = HomeVisit.builder()
                .patient(patient)
                .chw(chw)
                .visitDate(req.getVisitDate())
                .adherenceStatus(req.getAdherenceStatus())
                .pillCountRecorded(req.getPillCountRecorded())
                .pillCountExpected(req.getPillCountExpected())
                .pillCountDiscrepancy(discrepancy)
                .symptomsReported(req.getSymptomsReported())
                .sideEffectsReported(req.getSideEffectsReported())
                .psychosocialNotes(req.getPsychosocialNotes())
                .nextVisitDate(req.getNextVisitDate())
                .syncStatus(SyncStatus.PENDING)
                .build();

        visitRepository.save(visit);
        auditLogService.log("RECORD_VISIT", "home_visits", visit.getId());
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

    public HomeVisitResponse getVisit(UUID visitId) {
        Chw chw = resolveCurrentChw();
        HomeVisit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));
        if (!visit.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: visit not recorded by you");
        }
        return toResponse(visit);
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
                .adherenceStatus(v.getAdherenceStatus())
                .pillCountRecorded(v.getPillCountRecorded())
                .pillCountExpected(v.getPillCountExpected())
                .pillCountDiscrepancy(v.getPillCountDiscrepancy())
                .symptomsReported(v.getSymptomsReported())
                .sideEffectsReported(v.getSideEffectsReported())
                .psychosocialNotes(v.getPsychosocialNotes())
                .nextVisitDate(v.getNextVisitDate())
                .syncStatus(v.getSyncStatus().name())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

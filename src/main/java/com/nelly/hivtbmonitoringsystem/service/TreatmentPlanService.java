package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.AddDoseScheduleRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateTreatmentPlanRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateTreatmentPlanRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.DoseScheduleResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.TreatmentPlanResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.entity.TreatmentPlan;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.repository.TreatmentPlanRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreatmentPlanService {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final PatientRepository patientRepository;
    private final SystemUserRepository systemUserRepository;
    private final ChwRepository chwRepository;

    @Transactional
    public TreatmentPlanResponse createPlan(CreateTreatmentPlanRequest req) {
        SystemUser currentUser = resolveCurrentUser();
        Chw chw = resolveChw(currentUser);

        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }

        TreatmentPlan plan = TreatmentPlan.builder()
                .patient(patient)
                .medicationName(req.getMedicationName())
                .dosage(req.getDosage())
                .frequency(req.getFrequency())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isActive(true)
                .syncStatus(SyncStatus.PENDING)
                .createdBy(currentUser)
                .build();

        return toResponse(treatmentPlanRepository.save(plan));
    }

    public List<TreatmentPlanResponse> getPatientPlans(UUID patientId) {
        Chw chw = resolveChw(resolveCurrentUser());

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }

        return treatmentPlanRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).toList();
    }

    /** Used by patients to fetch their own treatment plans (no CHW auth check). */
    public List<TreatmentPlanResponse> getOwnPatientPlans(UUID patientId) {
        return treatmentPlanRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).toList();
    }

    public TreatmentPlanResponse getPlan(UUID planId) {
        TreatmentPlan plan = findAndAuthorize(planId);
        return toResponse(plan);
    }

    @Transactional
    public TreatmentPlanResponse updatePlan(UUID planId, UpdateTreatmentPlanRequest req) {
        TreatmentPlan plan = findAndAuthorize(planId);

        if (req.getMedicationName() != null) plan.setMedicationName(req.getMedicationName());
        if (req.getDosage() != null) plan.setDosage(req.getDosage());
        if (req.getFrequency() != null) plan.setFrequency(req.getFrequency());
        if (req.getEndDate() != null) plan.setEndDate(req.getEndDate());
        if (req.getIsActive() != null) {
            plan.setIsActive(req.getIsActive());
            if (!req.getIsActive()) {
                doseScheduleRepository.findByPlanIdAndIsActiveTrue(planId)
                        .forEach(s -> s.setIsActive(false));
            }
        }

        return toResponse(treatmentPlanRepository.save(plan));
    }

    @Transactional
    public DoseScheduleResponse addSchedule(UUID planId, AddDoseScheduleRequest req) {
        TreatmentPlan plan = findAndAuthorize(planId);
        SystemUser currentUser = resolveCurrentUser();

        DoseSchedule schedule = DoseSchedule.builder()
                .plan(plan)
                .patient(plan.getPatient())
                .doseTime(req.getDoseTime())
                .doseLabel(req.getDoseLabel())
                .notificationMethod(req.getNotificationMethod())
                .windowDurationMinutes(req.getWindowDurationMinutes())
                .isActive(true)
                .createdBy(currentUser)
                .build();

        return toScheduleResponse(doseScheduleRepository.save(schedule));
    }

    public List<DoseScheduleResponse> getSchedules(UUID planId) {
        findAndAuthorize(planId);
        return doseScheduleRepository.findByPlanId(planId)
                .stream().map(this::toScheduleResponse).toList();
    }

    @Transactional
    public DoseScheduleResponse deactivateSchedule(UUID scheduleId) {
        DoseSchedule schedule = doseScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        findAndAuthorize(schedule.getPlan().getId());
        schedule.setIsActive(false);
        return toScheduleResponse(doseScheduleRepository.save(schedule));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private TreatmentPlan findAndAuthorize(UUID planId) {
        TreatmentPlan plan = treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treatment plan not found"));

        Chw chw = resolveChw(resolveCurrentUser());
        if (!plan.getPatient().getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return plan;
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Chw resolveChw(SystemUser user) {
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
    }

    private TreatmentPlanResponse toResponse(TreatmentPlan plan) {
        List<DoseScheduleResponse> schedules = doseScheduleRepository.findByPlanId(plan.getId())
                .stream().map(this::toScheduleResponse).toList();

        return TreatmentPlanResponse.builder()
                .id(plan.getId())
                .patientId(plan.getPatient().getId())
                .patientName(plan.getPatient().getFullName())
                .medicationName(plan.getMedicationName())
                .dosage(plan.getDosage())
                .frequency(plan.getFrequency())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .isActive(plan.getIsActive())
                .syncStatus(plan.getSyncStatus())
                .createdAt(plan.getCreatedAt())
                .schedules(schedules)
                .build();
    }

    private DoseScheduleResponse toScheduleResponse(DoseSchedule s) {
        return DoseScheduleResponse.builder()
                .id(s.getId())
                .planId(s.getPlan().getId())
                .patientId(s.getPatient().getId())
                .doseTime(s.getDoseTime())
                .doseLabel(s.getDoseLabel())
                .notificationMethod(s.getNotificationMethod())
                .windowDurationMinutes(s.getWindowDurationMinutes())
                .isActive(s.getIsActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}

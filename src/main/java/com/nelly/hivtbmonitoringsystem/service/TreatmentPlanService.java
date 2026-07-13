package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.AddDoseScheduleRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateTreatmentPlanRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateTreatmentPlanRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.DoseScheduleResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.TreatmentPlanResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.entity.MedicationFormulary;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.entity.TreatmentPlan;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.repository.MedicationFormularyRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.repository.TreatmentPlanRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import com.nelly.hivtbmonitoringsystem.validation.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreatmentPlanService {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final MedicationFormularyRepository medicationFormularyRepository;
    private final PatientRepository patientRepository;
    private final SystemUserRepository systemUserRepository;
    private final ChwRepository chwRepository;
    private final SystemSettingsService systemSettingsService;
    private final AuditLogService auditLogService;
    private final ProviderAccessService providerAccessService;

    // ── Clinical staff writes ─────────────────────────────────────────────────

    @Transactional
    public TreatmentPlanResponse createPlan(CreateTreatmentPlanRequest req) {
        SystemUser currentUser = resolveCurrentUser();

        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        // Doctor-level scoping: only the managing provider (or an admin) can
        // prescribe for this patient.
        providerAccessService.ensureCanManage(patient);

        if (!"CONFIRMED".equals(patient.getRegistrationStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Patient must be clinically confirmed before a treatment plan can be created");
        }

        if (req.getEndDate() != null && !req.getEndDate().isAfter(req.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date must be after start date");
        }

        MedicationFormulary medication = medicationFormularyRepository.findById(req.getMedicationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Selected medication was not found in the formulary"));
        if (!Boolean.TRUE.equals(medication.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Selected medication is no longer active in the formulary");
        }

        TreatmentPlan plan = TreatmentPlan.builder()
                .patient(patient)
                .medication(medication)
                .dosage(req.getDosage())
                .frequency(req.getFrequency())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isActive(true)
                .syncStatus(SyncStatus.PENDING)
                .createdBy(currentUser)
                .build();

        plan = treatmentPlanRepository.save(plan);
        auditLogService.log("CREATE_TREATMENT_PLAN", "treatment_plans", plan.getId());

        if (req.getDoseTimes() != null && !req.getDoseTimes().isEmpty()) {
            ConfirmationChannel method = Boolean.TRUE.equals(patient.getHasSmartphone())
                    ? ConfirmationChannel.APP : ConfirmationChannel.SMS;
            int windowMinutes = systemSettingsService.get().getConfirmWindowMinutes();

            int i = 1;
            for (LocalTime doseTime : req.getDoseTimes()) {
                buildAndSaveSchedule(plan, patient, doseTime, "Dose " + i, method, windowMinutes, null, currentUser);
                i++;
            }
        }

        return toResponse(plan);
    }

    @Transactional
    public TreatmentPlanResponse updatePlan(UUID planId, UpdateTreatmentPlanRequest req) {
        TreatmentPlan plan = findPlan(planId);
        providerAccessService.ensureCanManage(plan.getPatient());

        if (req.getMedicationId() != null) {
            MedicationFormulary medication = medicationFormularyRepository.findById(req.getMedicationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Selected medication was not found in the formulary"));
            if (!Boolean.TRUE.equals(medication.getIsActive())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Selected medication is no longer active in the formulary");
            }
            plan.setMedication(medication);
        }
        if (req.getDosage() != null) plan.setDosage(req.getDosage());
        if (req.getFrequency() != null) plan.setFrequency(req.getFrequency());
        if (req.getEndDate() != null) {
            if (req.getEndDate().isBefore(plan.getStartDate())) {
                throw new BusinessRuleException("endDate",
                        "End date cannot be before the treatment start date (" + plan.getStartDate() + ")",
                        HttpStatus.BAD_REQUEST);
            }
            plan.setEndDate(req.getEndDate());
        }
        if (req.getIsActive() != null) {
            plan.setIsActive(req.getIsActive());
            if (!req.getIsActive()) {
                doseScheduleRepository.findByPlanIdAndIsActiveTrue(planId)
                        .forEach(s -> s.setIsActive(false));
            }
        }

        TreatmentPlan saved = treatmentPlanRepository.save(plan);
        auditLogService.log("UPDATE_TREATMENT_PLAN", "treatment_plans", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public DoseScheduleResponse addSchedule(UUID planId, AddDoseScheduleRequest req) {
        TreatmentPlan plan = findPlan(planId);
        providerAccessService.ensureCanManage(plan.getPatient());
        SystemUser currentUser = resolveCurrentUser();

        DoseSchedule schedule = buildAndSaveSchedule(plan, plan.getPatient(), req.getDoseTime(),
                req.getDoseLabel(), req.getNotificationMethod(),
                systemSettingsService.get().getConfirmWindowMinutes(),
                req.getPrescriptionSource(), currentUser);
        auditLogService.log("ADD_DOSE_SCHEDULE", "dose_schedules", schedule.getId());

        return toScheduleResponse(schedule);
    }

    /** Shared by the manual addSchedule endpoint and createPlan's auto-generation from doseTimes. */
    private DoseSchedule buildAndSaveSchedule(TreatmentPlan plan, Patient patient, LocalTime doseTime,
                                               String doseLabel, ConfirmationChannel notificationMethod,
                                               Integer windowDurationMinutes, String prescriptionSource,
                                               SystemUser currentUser) {
        DoseSchedule schedule = DoseSchedule.builder()
                .plan(plan)
                .patient(patient)
                .doseTime(doseTime)
                .doseLabel(doseLabel)
                .notificationMethod(notificationMethod)
                .windowDurationMinutes(windowDurationMinutes)
                .isActive(true)
                .createdBy(currentUser)
                .prescriptionSource(prescriptionSource)
                .build();

        return doseScheduleRepository.save(schedule);
    }

    @Transactional
    public DoseScheduleResponse deactivateSchedule(UUID scheduleId) {
        DoseSchedule schedule = doseScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        schedule.setIsActive(false);
        DoseSchedule saved = doseScheduleRepository.save(schedule);
        auditLogService.log("DEACTIVATE_DOSE_SCHEDULE", "dose_schedules", saved.getId());
        return toScheduleResponse(saved);
    }

    public List<com.nelly.hivtbmonitoringsystem.dto.response.MedicationFormularyResponse> getActiveMedications() {
        return medicationFormularyRepository.findByIsActiveTrue().stream()
                .map(m -> com.nelly.hivtbmonitoringsystem.dto.response.MedicationFormularyResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .dosageForm(m.getDosageForm())
                        .build())
                .toList();
    }

    // ── Shared reads (CHW sees own patients only; clinical sees all) ──────────

    public List<TreatmentPlanResponse> getPatientPlans(UUID patientId) {
        SystemUser currentUser = resolveCurrentUser();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (currentUser.getRole() == UserRole.CHW) {
            assertChwOwnsPatient(currentUser, patient);
        }

        return treatmentPlanRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).toList();
    }

    public TreatmentPlanResponse getPlan(UUID planId) {
        TreatmentPlan plan = findPlan(planId);
        SystemUser currentUser = resolveCurrentUser();

        if (currentUser.getRole() == UserRole.CHW) {
            assertChwOwnsPatient(currentUser, plan.getPatient());
        }

        return toResponse(plan);
    }

    public List<DoseScheduleResponse> getSchedules(UUID planId) {
        TreatmentPlan plan = findPlan(planId);
        SystemUser currentUser = resolveCurrentUser();

        if (currentUser.getRole() == UserRole.CHW) {
            assertChwOwnsPatient(currentUser, plan.getPatient());
        }

        return doseScheduleRepository.findByPlanId(planId)
                .stream().map(this::toScheduleResponse).toList();
    }

    /** All active schedules for a patient — used by CHW during home visits. */
    public List<DoseScheduleResponse> getActiveSchedulesForPatient(UUID patientId) {
        SystemUser currentUser = resolveCurrentUser();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (currentUser.getRole() == UserRole.CHW) {
            assertChwOwnsPatient(currentUser, patient);
        }

        return doseScheduleRepository.findByPatientIdAndIsActiveTrue(patientId)
                .stream().map(this::toScheduleResponse).toList();
    }

    /** Used by patients to fetch their own treatment plans (no ownership check). */
    public List<TreatmentPlanResponse> getOwnPatientPlans(UUID patientId) {
        return treatmentPlanRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TreatmentPlan findPlan(UUID planId) {
        return treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Treatment plan not found"));
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void assertChwOwnsPatient(SystemUser user, Patient patient) {
        Chw chw = chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }
    }

    private TreatmentPlanResponse toResponse(TreatmentPlan plan) {
        List<DoseScheduleResponse> schedules = doseScheduleRepository.findByPlanId(plan.getId())
                .stream().map(this::toScheduleResponse).toList();

        return TreatmentPlanResponse.builder()
                .id(plan.getId())
                .patientId(plan.getPatient().getId())
                .patientName(plan.getPatient().getFullName())
                .medicationId(plan.getMedication().getId())
                .medicationName(plan.getMedication().getName())
                .dosage(plan.getDosage())
                .frequency(plan.getFrequency())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .isActive(plan.getIsActive())
                .syncStatus(plan.getSyncStatus())
                .createdByName(plan.getCreatedBy() != null ? plan.getCreatedBy().getFullName() : null)
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
                .createdByName(s.getCreatedBy() != null ? s.getCreatedBy().getFullName() : null)
                .prescriptionSource(s.getPrescriptionSource())
                .createdAt(s.getCreatedAt())
                .build();
    }
}

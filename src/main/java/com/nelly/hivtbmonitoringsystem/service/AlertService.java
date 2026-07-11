package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.CreateAlertRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ReportSyncFailureRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AlertResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final FacilityProviderRepository facilityProviderRepository;
    private final SystemUserRepository systemUserRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuditLogService auditLogService;
    private final SystemSettingsService systemSettingsService;
    private final EmailService emailService;
    private final HomeVisitTaskService homeVisitTaskService;

    // ── CHW ──────────────────────────────────────────────────────────────────

    public List<AlertResponse> getChwAlerts() {
        Chw chw = resolveChw();
        return alertRepository.findByChwIdAndIsResolvedFalseOrderByCreatedAtDesc(chw.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<AlertResponse> getChwPatientAlerts(UUID patientId) {
        Chw chw = resolveChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }
        return alertRepository.findByPatientIdAndIsResolvedFalseOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── Clinical / facility provider ─────────────────────────────────────────

    public List<AlertResponse> getClinicalAlerts() {
        List<AlertSeverity> sevs = List.of(AlertSeverity.CRITICAL, AlertSeverity.WARNING);
        UUID facilityId = resolveFacilityScopeOrNull();
        List<Alert> alerts = facilityId == null
                ? alertRepository.findBySeverityInAndIsResolvedFalseOrderByCreatedAtDesc(sevs)
                : alertRepository.findFacilityClinicalActive(facilityId, sevs);
        return alerts.stream().map(this::toResponse).toList();
    }

    /**
     * The facility a clinical caller is scoped to, or null for system-wide access.
     * ADMIN/SYSTEM_ADMIN see every facility; a facility provider / clinical staff
     * member sees only their own facility's patients' alerts (mirrors the CHW
     * seeing only their own patients). A caller with no facility profile (e.g. a
     * supervisor, who has separate supervisor views) keeps the unscoped behavior.
     */
    private UUID resolveFacilityScopeOrNull() {
        SystemUser user = resolveCurrentUser();
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SYSTEM_ADMIN) return null;
        return facilityProviderRepository.findByUserId(user.getId())
                .map(p -> p.getFacility().getId())
                .orElse(null);
    }

    public List<AlertResponse> getClinicalPatientAlerts(UUID patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        return alertRepository.findByPatientIdAndIsResolvedFalseOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── State mutations ───────────────────────────────────────────────────────

    @Transactional
    public AlertResponse markRead(UUID alertId) {
        Alert alert = findAndAuthorizeWrite(alertId);
        alert.setIsRead(true);
        Alert saved = alertRepository.save(alert);
        auditLogService.log("ALERT_ACKNOWLEDGED", "alerts", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public AlertResponse markResolved(UUID alertId) {
        Alert alert = findAndAuthorizeWrite(alertId);
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolveCurrentUser());
        Alert saved = alertRepository.save(alert);
        auditLogService.log("ALERT_RESOLVED", "alerts", saved.getId());
        return toResponse(saved);
    }

    /**
     * Auto-resolves every open alert of {@code type} for a patient once the
     * underlying condition has actually cleared — e.g. a MISSED_DOSE alert when
     * the patient confirms a dose, or a NEW_PATIENT_ASSIGNMENT alert when the CHW
     * accepts. Called by the flow that clears the condition, not by a resolver
     * clicking a button, so it skips the ownership check and records the acting
     * user (system triggers may have no user — then {@code resolvedBy} is null).
     */
    @Transactional
    public void autoResolvePatientAlerts(UUID patientId, AlertType type) {
        List<Alert> open = alertRepository.findByPatientIdAndAlertTypeAndIsResolvedFalse(patientId, type);
        if (open.isEmpty()) return;
        SystemUser actor = resolveCurrentUserOrNull();
        LocalDateTime now = LocalDateTime.now();
        for (Alert a : open) {
            a.setIsResolved(true);
            a.setResolvedAt(now);
            a.setResolvedBy(actor);
            alertRepository.save(a);
            auditLogService.log("ALERT_AUTO_RESOLVED", "alerts", a.getId());
        }
    }

    /** Resolved CRITICAL/WARNING alerts — the clinical "Resolved" history view (facility-scoped). */
    public List<AlertResponse> getResolvedClinicalAlerts() {
        List<AlertSeverity> sevs = List.of(AlertSeverity.CRITICAL, AlertSeverity.WARNING);
        UUID facilityId = resolveFacilityScopeOrNull();
        List<Alert> alerts = facilityId == null
                ? alertRepository.findBySeverityInAndIsResolvedTrueOrderByResolvedAtDesc(sevs)
                : alertRepository.findFacilityClinicalResolved(facilityId, sevs);
        return alerts.stream().map(this::toResponse).toList();
    }

    private void broadcast(Alert saved) {
        messagingTemplate.convertAndSend("/topic/alerts", toResponse(saved));
    }

    /**
     * Relays an alert already persisted by the AI microservice (which writes directly
     * to the shared database and has no WebSocket session of its own) onto the
     * live /topic/alerts feed, without inserting a duplicate row.
     */
    public void broadcastExternalAlert(AlertResponse response) {
        messagingTemplate.convertAndSend("/topic/alerts", response);
    }

    // ── Reported by the mobile app's offline sync queue ───────────────────────

    /**
     * Raised when a CHW's or patient's offline-queued action (home visit, dose
     * confirmation) is rejected by the server in a way retrying cannot fix.
     * Reuses the existing alert pipeline so it surfaces on the CHW's own feed,
     * the facility/clinical feed, and the supervisor's facility-scoped feed —
     * the same places any other WARNING alert would.
     */
    @Transactional
    public AlertResponse reportSyncFailure(ReportSyncFailureRequest req) {
        SystemUser caller = resolveCurrentUser();

        Patient patient;
        Chw chw;
        if (caller.getRole() == UserRole.CHW) {
            chw = chwRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (req.getPatientId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "patientId is required when reporting a HOME_VISIT sync failure");
            }
            patient = patientRepository.findById(req.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
            if (!patient.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
            }
        } else if (caller.getRole() == UserRole.PATIENT) {
            patient = patientRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient profile not found"));
            chw = patient.getChw();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only a CHW or patient can report a sync failure");
        }

        String actionLabel = "HOME_VISIT".equals(req.getActionType()) ? "home visit" : "dose confirmation";
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .alertType(AlertType.SYNC_FAILURE)
                .severity(AlertSeverity.WARNING)
                .title("Offline Sync Failed — " + patient.getFullName())
                .message(String.format(
                        "A %s recorded offline for %s could not be saved after sync: %s",
                        actionLabel, patient.getFullName(), req.getReason()))
                .isRead(false)
                .isResolved(false)
                .build();

        Alert saved = alertRepository.save(alert);
        broadcast(saved);
        auditLogService.log("SYNC_FAILURE_REPORTED", "alerts", saved.getId());
        return toResponse(saved);
    }

    // ── Called internally by PatientService / PatientAssignmentScheduler ─────

    /**
     * In-app notification to the screening CHW that their referral was
     * confirmed and joined their active caseload (PROVISIONAL → CONFIRMED,
     * called once from PatientService#confirmPatient). Broadcast over the
     * WebSocket relay like every other alert.
     */
    @Transactional
    public void createReferralConfirmedAlert(Patient patient, Chw chw) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .alertType(AlertType.REFERRAL_CONFIRMED)
                .severity(AlertSeverity.INFO)
                .title("Referral Confirmed")
                .message("Your referred patient " + patient.getFullName()
                        + " (" + patient.getPatientCode() + ") has been confirmed "
                        + "and added to your caseload.")
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    /**
     * Masked notification — deliberately does NOT link the patient, so the
     * existing toResponse() mapping below returns patientName=null. The CHW
     * sees only the protocol type until they accept via PatientController.
     */
    @Transactional
    public void createPatientAssignmentAlert(Chw chw, String protocol) {
        Alert alert = Alert.builder()
                .chw(chw)
                .alertType(AlertType.NEW_PATIENT_ASSIGNMENT)
                .severity(AlertSeverity.WARNING)
                .title("New Patient Assignment")
                .message("New patient assignment in your village. Protocol: " + protocol +
                        ". Action required within 24h.")
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    /** 48h escalation — supervisor visibility, so full patient detail is included. */
    @Transactional
    public void createPatientAssignmentEscalatedAlert(Patient patient, Chw chw) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .alertType(AlertType.NEW_PATIENT_ASSIGNMENT)
                .severity(AlertSeverity.CRITICAL)
                .title("Patient Assignment Not Accepted — " + patient.getFullName())
                .message(String.format(
                        "CHW %s has not accepted the assignment for patient %s (%s) within 48 hours. " +
                        "Supervisor follow-up required.",
                        chw.getUser().getFullName(), patient.getFullName(), patient.getPatientCode()))
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    // ── Internal — called by AI microservice (SYSTEM_ADMIN) ──────────────────

    @Transactional
    public AlertResponse createAlert(CreateAlertRequest req) {
        Alert.AlertBuilder builder = Alert.builder()
                .alertType(req.getAlertType())
                .severity(req.getSeverity())
                .title(req.getTitle())
                .message(req.getMessage())
                .isRead(false)
                .isResolved(false);

        if (req.getPatientId() != null) {
            builder.patient(patientRepository.findById(req.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found")));
        }
        if (req.getChwId() != null) {
            builder.chw(chwRepository.findById(req.getChwId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHW not found")));
        }
        if (req.getProviderId() != null) {
            builder.provider(facilityProviderRepository.findById(req.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found")));
        }

        Alert saved = alertRepository.save(builder.build());
        broadcast(saved);
        return toResponse(saved);
    }

    // ── Called internally by MissedDoseScheduler ─────────────────────────────

    /**
     * No alert for a single isolated miss — only once the same patient has
     * missed the same dose schedule on two consecutive occasions. Severity
     * then escalates further with the streak: WARNING below the
     * admin-configured missed_dose_threshold, CRITICAL once it's reached or
     * exceeded.
     */
    @Transactional
    public void createMissedDoseAlert(Patient patient, TreatmentPlan plan, int consecutiveMissed) {
        if (consecutiveMissed < 2) return;

        int threshold = systemSettingsService.get().getMissedDoseThreshold();
        AlertSeverity severity = consecutiveMissed >= threshold ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;
        String message = String.format(
                "Patient %s missed their %s dose scheduled for %s. Consecutive misses: %d%s.",
                patient.getFullName(),
                plan.getMedication().getName(),
                LocalDate.now(),
                consecutiveMissed,
                consecutiveMissed >= threshold ? " (threshold reached — escalated)" : "");

        // One living alert per patient for an ongoing streak: if an open missed-dose
        // alert already exists, update it in place (bump the streak/severity and
        // re-surface as unread) instead of inserting a duplicate row every day.
        Alert existing = alertRepository
                .findFirstByPatientIdAndAlertTypeAndIsResolvedFalseOrderByCreatedAtDesc(
                        patient.getId(), AlertType.MISSED_DOSE)
                .orElse(null);
        if (existing != null) {
            existing.setSeverity(severity);
            existing.setMessage(message);
            existing.setIsRead(false);
            broadcast(alertRepository.save(existing));
            return;
        }

        Alert alert = Alert.builder()
                .patient(patient)
                .chw(patient.getChw())
                .alertType(AlertType.MISSED_DOSE)
                .severity(severity)
                .title("Missed Dose — " + patient.getFullName())
                .message(message)
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    // ── Called internally by HomeVisitService ────────────────────────────────

    /** Fired for a CTCAE Grade 3/4 adverse event recorded during a CHW home visit — always CRITICAL. */
    @Transactional
    public void createAdverseEventAlert(Patient patient, Chw chw, HomeVisit visit) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .alertType(AlertType.ADVERSE_EVENT)
                .severity(AlertSeverity.CRITICAL)
                .title("Severe Adverse Event — " + patient.getFullName())
                .message(String.format(
                        "Patient %s (%s) reported a Grade %d adverse drug reaction during a home visit on %s. " +
                        "Immediate clinical review required.",
                        patient.getFullName(), patient.getPatientCode(),
                        visit.getAdverseEventGrade(), visit.getVisitDate().toLocalDate()))
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));

        // Open a follow-up in-person home-visit task for the severe reaction.
        homeVisitTaskService.createTask(patient, HomeVisitTaskService.SIDE_EFFECT,
                "Grade " + visit.getAdverseEventGrade() + " adverse drug reaction");

        // CRITICAL severity — also notify Clinical Staff at the patient's facility by
        // email, not just the in-app/WebSocket broadcast (mirrors the IIT/treatment-
        // interrupted alert pattern in NotificationService).
        if (patient.getFacility() != null) {
            String visitDate = visit.getVisitDate().toLocalDate().toString();
            facilityProviderRepository.findByFacilityId(patient.getFacility().getId()).forEach(provider -> {
                SystemUser providerUser = provider.getUser();
                if (providerUser != null && providerUser.getEmail() != null) {
                    emailService.sendAdverseEventAlert(
                            providerUser.getEmail(),
                            providerUser.getFullName(),
                            patient.getFullName(),
                            patient.getPatientCode(),
                            chw.getUser().getFullName(),
                            visit.getAdverseEventGrade(),
                            visitDate);
                }
            });
        }
    }

    // ── Called internally by TracingTaskService ──────────────────────────────

    @Transactional
    public void createIitEscalatedAlert(Patient patient, Chw chw, TracingTask task, AlertSeverity severity) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .alertType(AlertType.IIT_ESCALATED)
                .severity(severity)
                .title("LTFU Tracing Required — " + patient.getFullName())
                .message(String.format(
                        "Patient %s (%s) missed their facility appointment on %s. " +
                        "Days since missed: %d. Reason: %s. Please conduct a tracing visit.",
                        patient.getFullName(), patient.getPatientCode(),
                        task.getMissedAppointmentDate(), task.getDaysSinceMissed(), task.getReason()))
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    @Transactional
    public void createTracingResolvedAlert(Patient patient, Chw chw, FacilityProvider provider, TracingTask task) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .provider(provider)
                .alertType(AlertType.LTFU_TRACING_RESOLVED)
                .severity(AlertSeverity.INFO)
                .title("Tracing Resolved — " + patient.getFullName())
                .message(String.format(
                        "Tracing task for patient %s (%s) was resolved by CHW %s. " +
                        "Outcome: %s. %s",
                        patient.getFullName(), patient.getPatientCode(),
                        chw.getUser().getFullName(), task.getOutcome(),
                        task.getResolutionPlan() != null ? "Plan: " + task.getResolutionPlan() : ""))
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    @Transactional
    public void createTreatmentInterruptedAlert(Patient patient, Chw chw, TracingTask task) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(chw)
                .alertType(AlertType.TREATMENT_INTERRUPTED)
                .severity(AlertSeverity.CRITICAL)
                .title("LTFU Confirmed — " + patient.getFullName())
                .message(String.format(
                        "Patient %s (%s) is officially Lost to Follow-Up after %d days without contact " +
                        "since %s. Status escalated to supervisor. Immediate action required.",
                        patient.getFullName(), patient.getPatientCode(),
                        task.getDaysSinceMissed(), task.getMissedAppointmentDate()))
                .isRead(false)
                .isResolved(false)
                .build();
        broadcast(alertRepository.save(alert));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Alert findAndAuthorizeWrite(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));

        SystemUser caller = resolveCurrentUser();
        if (caller.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (alert.getChw() == null || !alert.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Alert is not assigned to you");
            }
        }
        return alert;
    }

    private Chw resolveChw() {
        return chwRepository.findByUserId(resolveCurrentUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /** Current user if there is an authenticated one, else null (system-triggered auto-resolve). */
    private SystemUser resolveCurrentUserOrNull() {
        try {
            String email = SecurityUtil.getCurrentUserEmail();
            if (email == null) return null;
            return systemUserRepository.findByEmail(email).orElse(null);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private AlertResponse toResponse(Alert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .patientName(a.getPatient() != null ? a.getPatient().getFullName() : null)
                .chwId(a.getChw() != null ? a.getChw().getId() : null)
                .alertType(a.getAlertType())
                .severity(a.getSeverity())
                .title(a.getTitle())
                .message(a.getMessage())
                .isRead(a.getIsRead())
                .isResolved(a.getIsResolved())
                .resolvedAt(a.getResolvedAt())
                .resolvedByName(a.getResolvedBy() != null ? a.getResolvedBy().getFullName() : null)
                .createdAt(a.getCreatedAt())
                .build();
    }
}

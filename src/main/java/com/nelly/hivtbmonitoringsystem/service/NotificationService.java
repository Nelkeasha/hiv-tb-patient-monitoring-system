package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Unified notification dispatcher — Update 5.
 *
 * Every notification event is sent through ALL three channels simultaneously:
 *   1. In-app alert  — AlertService (persisted in DB, shown in mobile/web)
 *   2. Email         — EmailService (async SMTP)
 *   3. Push (FCM)    — FcmService   (Firebase Cloud Messaging, async)
 *
 * Channels 2 and 3 are no-ops when not configured (MAIL_ENABLED=false
 * or FIREBASE_SERVICE_ACCOUNT_JSON not set). The app runs normally.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AlertService alertService;
    private final EmailService emailService;
    private final FcmService fcmService;
    private final SmsOutboundService smsOutboundService;
    private final SystemUserRepository userRepository;
    private final com.nelly.hivtbmonitoringsystem.repository.FacilityProviderRepository facilityProviderRepository;
    private final SystemSettingsService systemSettingsService;

    // ── LTFU Events ───────────────────────────────────────────────────────────

    /**
     * Fired by LtfuScheduler when a task first becomes LATE (day 0).
     * All 3 channels to CHW.
     */
    public void notifyLtfuLate(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app alert
        alertService.createIitEscalatedAlert(patient, chw, task, AlertSeverity.WARNING);

        // 2. Email to CHW
        String chwEmail = chw.getUser().getEmail();
        if (chwEmail != null) {
            emailService.sendLtfuLateAlert(
                    chwEmail,
                    chw.getUser().getFullName(),
                    patient.getFullName(),
                    patient.getPatientCode(),
                    task.getMissedAppointmentDate().toString(),
                    task.getDaysSinceMissed()
            );
        }

        // 3. FCM push to CHW device
        String chwFcmToken = chw.getUser().getFcmToken();
        if (chwFcmToken != null) {
            fcmService.sendLtfuLateAlert(chwFcmToken, patient.getFullName(), task.getDaysSinceMissed());
        }

        log.info("LTFU LATE notification sent: patient={} chw={}", patient.getId(), chw.getId());
    }

    /**
     * Fired by LtfuScheduler when task transitions to IIT_ESCALATED (day 14).
     * All 3 channels to CHW — marked CRITICAL/URGENT.
     */
    public void notifyIitEscalated(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app CRITICAL alert
        alertService.createIitEscalatedAlert(patient, chw, task, AlertSeverity.CRITICAL);

        // 2. Email to CHW
        String chwEmail = chw.getUser().getEmail();
        if (chwEmail != null) {
            emailService.sendIitEscalatedAlert(
                    chwEmail,
                    chw.getUser().getFullName(),
                    patient.getFullName(),
                    patient.getPatientCode(),
                    patient.getVillage() != null ? patient.getVillage() : "Unknown Village",
                    task.getDaysSinceMissed()
            );
        }

        // 3. FCM priority push to CHW device
        String chwFcmToken = chw.getUser().getFcmToken();
        if (chwFcmToken != null) {
            fcmService.sendIitEscalated(chwFcmToken, patient.getFullName(), task.getDaysSinceMissed());
        }

        log.info("LTFU IIT_ESCALATED notification sent: patient={}", patient.getId());
    }

    /**
     * Fired when a patient's treatment is officially confirmed interrupted (day 30+).
     * All 3 channels to CHW + all active supervisors.
     */
    public void notifyTreatmentInterrupted(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app CRITICAL alert
        alertService.createTreatmentInterruptedAlert(patient, chw, task);

        // 2. Email + FCM to CHW
        String chwEmail = chw.getUser().getEmail();
        if (chwEmail != null) {
            emailService.sendTreatmentInterruptedAlert(
                    chwEmail, chw.getUser().getFullName(),
                    patient.getFullName(), patient.getPatientCode(),
                    chw.getUser().getFullName(),
                    task.getDaysSinceMissed(),
                    task.getMissedAppointmentDate().toString()
            );
        }
        String chwFcm = chw.getUser().getFcmToken();
        if (chwFcm != null) {
            fcmService.sendTreatmentInterrupted(chwFcm, patient.getFullName());
        }

        // 3. Email + FCM to all active supervisors
        userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("SUPERVISOR") && Boolean.TRUE.equals(u.getIsActive()))
                .forEach(supervisor -> {
                    emailService.sendTreatmentInterruptedAlert(
                            supervisor.getEmail(),
                            supervisor.getFullName(),
                            patient.getFullName(),
                            patient.getPatientCode(),
                            chw.getUser().getFullName(),
                            task.getDaysSinceMissed(),
                            task.getMissedAppointmentDate().toString()
                    );
                    if (supervisor.getFcmToken() != null) {
                        fcmService.sendTreatmentInterrupted(supervisor.getFcmToken(), patient.getFullName());
                    }
                });

        log.info("TREATMENT_INTERRUPTED notification sent: patient={}", patient.getId());
    }

    /**
     * Fired when a CHW resolves a tracing task (3.4.4).
     * Notifies all active facility providers at the patient's facility.
     */
    public void notifyTracingResolved(Patient patient, Chw chw, TracingTask task) {
        if (patient.getFacility() == null) return;

        facilityProviderRepository.findByFacilityId(patient.getFacility().getId()).forEach(provider -> {
            // 1. In-app alert
            alertService.createTracingResolvedAlert(patient, chw, provider, task);

            // 2. Email to provider
            SystemUser providerUser = provider.getUser();
            if (providerUser != null && providerUser.getEmail() != null) {
                emailService.sendTracingResolvedAlert(
                        providerUser.getEmail(),
                        providerUser.getFullName(),
                        patient.getFullName(),
                        patient.getPatientCode(),
                        chw.getUser().getFullName(),
                        task.getOutcome(),
                        task.getResolutionPlan()
                );
            }

            // 3. FCM push to provider
            if (providerUser != null && providerUser.getFcmToken() != null) {
                fcmService.sendGeneric(providerUser.getFcmToken(),
                        "Tracing Resolved — " + patient.getFullName(),
                        "Outcome: " + task.getOutcome(), "LTFU_TRACING_RESOLVED");
            }
        });

        log.info("TRACING_RESOLVED notification sent: patient={} task={}", patient.getId(), task.getId());
    }

    // ── Missed Dose Events ────────────────────────────────────────────────────

    /**
     * Fired by MissedDoseScheduler/SmsConfirmationService for every missed dose.
     * CHW gets FCM push immediately for every miss.
     * CHW gets email only once the consecutive-miss streak reaches the
     * admin-configured missed_dose_threshold (avoids fatigue on a single miss).
     */
    public void notifyMissedDose(Patient patient, TreatmentPlan plan, int consecutiveMissed) {
        // 1. In-app alert — severity escalates with the streak (see AlertService)
        alertService.createMissedDoseAlert(patient, plan, consecutiveMissed);

        Chw chw = patient.getChw();
        if (chw == null) return;

        // 2. FCM push for every single missed dose
        String chwFcm = chw.getUser().getFcmToken();
        if (chwFcm != null) {
            fcmService.sendMissedDoseAlert(chwFcm, patient.getFullName(), plan.getMedication().getName());
        }

        // 3. Email only once the streak reaches the configured threshold
        int threshold = systemSettingsService.get().getMissedDoseThreshold();
        if (consecutiveMissed >= threshold && chw.getUser().getEmail() != null) {
            emailService.sendMissedDoseSummary(
                    chw.getUser().getEmail(),
                    chw.getUser().getFullName(),
                    patient.getFullName(),
                    patient.getPatientCode(),
                    plan.getMedication().getName(),
                    consecutiveMissed
            );
        }
    }

    // ── False Confirmation Events ─────────────────────────────────────────────

    /**
     * Fired by AI engine when suspicion score reaches threshold 2.
     * Clinical staff get all 3 channels.
     */
    public void notifyFalseConfirmation(Patient patient, String suspicionReason) {
        // 1. In-app alert via AlertService (AI writes directly to alerts table)

        // 2. Email + FCM to all active facility providers
        if (patient.getFacility() != null) {
            userRepository.findAll().stream()
                    .filter(u -> (u.getRole().name().equals("FACILITY_PROVIDER") ||
                                  u.getRole().name().equals("CLINICAL_STAFF")) &&
                                 Boolean.TRUE.equals(u.getIsActive()))
                    .forEach(provider -> {
                        emailService.sendFalseConfirmationAlert(
                                provider.getEmail(),
                                provider.getFullName(),
                                patient.getFullName(),
                                patient.getPatientCode(),
                                suspicionReason
                        );
                        if (provider.getFcmToken() != null) {
                            fcmService.sendFalseConfirmationAlert(
                                    provider.getFcmToken(),
                                    patient.getFullName(),
                                    suspicionReason
                            );
                        }
                    });
        }
        log.info("FALSE_CONFIRMATION notification sent: patient={}", patient.getId());
    }

    // ── Patient Confirmed (Route B) ───────────────────────────────────────────

    public void notifyPatientConfirmed(Patient patient, Chw chw, String confirmedByName) {
        if (chw == null || chw.getUser() == null) {
            log.info("PATIENT_CONFIRMED: no CHW assigned to patient={}, skipping CHW notification", patient.getId());
            return;
        }

        // 1. In-app alert — email/SMS are disabled in this deployment, so this
        // (plus the WebSocket broadcast inside AlertService) is the channel the
        // CHW reliably sees. Fires once: confirmPatient guards PROVISIONAL→CONFIRMED.
        alertService.createReferralConfirmedAlert(patient, chw);

        String diagnosisName = patient.getDiagnosisType() != null
                ? patient.getDiagnosisType().name().replace("_", " ")
                : "Unknown";
        String treatmentStart = (patient.getArtStartDate() != null
                ? patient.getArtStartDate()
                : patient.getTbTreatmentStartDate()) != null
                ? (patient.getArtStartDate() != null
                        ? patient.getArtStartDate().toString()
                        : patient.getTbTreatmentStartDate().toString())
                : "Not yet set";
        String facilityName = patient.getFacility() != null ? patient.getFacility().getName() : "Facility";

        // Email to CHW
        String chwEmail = chw.getUser().getEmail();
        if (chwEmail != null) {
            emailService.sendPatientConfirmedAlert(
                    chwEmail,
                    chw.getUser().getFullName(),
                    patient.getFullName(),
                    patient.getReferralId() != null ? patient.getReferralId() : "N/A",
                    diagnosisName,
                    treatmentStart,
                    confirmedByName,
                    facilityName
            );
        }

        // FCM push to CHW
        String chwFcmToken = chw.getUser().getFcmToken();
        if (chwFcmToken != null) {
            fcmService.sendPatientConfirmedAlert(chwFcmToken, patient.getFullName(), diagnosisName);
        }

        log.info("PATIENT_CONFIRMED notification sent: patient={} chw={}", patient.getId(), chw.getId());
    }

    // ── Prevention referral after a NEGATIVE screening result (RBC 2022) ──────

    /** Pushes a prevention-mode directive to the CHW's phone (TB differential / HIV PrEP). */
    public void notifyPreventionReferral(Patient patient, String title, String body) {
        Chw chw = patient.getChw();
        if (chw == null || chw.getUser() == null) {
            log.info("PREVENTION_REFERRAL: no CHW for patient={}, skipping push", patient.getId());
            return;
        }
        String token = chw.getUser().getFcmToken();
        if (token != null) {
            fcmService.sendGeneric(token, title, body, "PREVENTION_REFERRAL");
        }
        log.info("PREVENTION_REFERRAL push queued: patient={} chw={}", patient.getId(), chw.getId());
    }

    // ── Patient App Account Created ───────────────────────────────────────────

    public void notifyPatientAccountCreated(String phone, String fullName,
                                            String loginEmail, String tempPassword) {
        smsOutboundService.sendPatientWelcome(phone, fullName, loginEmail, tempPassword);
        log.info("Patient welcome SMS dispatched to: {}", phone);
    }

    // ── User Management Events ────────────────────────────────────────────────

    public void notifyNewUser(String email, String fullName, String role, String tempPassword) {
        emailService.sendWelcomeEmail(email, fullName, role, tempPassword);
        log.info("Welcome email dispatched to: {}", email);
    }

    public void notifyPasswordReset(String email, String fullName, String tempPassword) {
        emailService.sendPasswordResetEmail(email, fullName, tempPassword);
        log.info("Password reset email dispatched to: {}", email);
    }

    // ── Village → CHW Assignment (self-presented facility patients) ───────────

    /**
     * Fired by PatientService#registerPatient when a self-presented patient is
     * matched to a CHW by village/sector. In-app + push only — deliberately no
     * email (a shared family phone makes email less private than a masked push)
     * and no patient name/diagnosis in either channel until the CHW accepts.
     */
    public void notifyNewPatientAssignment(Patient patient, Chw chw) {
        String protocol = protocolLabel(patient.getDiagnosisType());

        alertService.createPatientAssignmentAlert(chw, protocol);

        String chwFcmToken = chw.getUser().getFcmToken();
        if (chwFcmToken != null) {
            fcmService.sendGeneric(chwFcmToken,
                    "New Patient Assignment",
                    "New patient assignment in your village. Protocol: " + protocol + ". Action required within 24h.",
                    "NEW_PATIENT_ASSIGNMENT");
        }

        log.info("New patient assignment notification sent (masked): chw={}", chw.getId());
    }

    /** Fired by PatientAssignmentScheduler at the 24h mark if still not accepted. SMS + push to the CHW. */
    public void notifyPatientAssignmentReminder(Chw chw) {
        String chwPhone = chw.getUser().getPhoneNumber();
        if (chwPhone != null) {
            smsOutboundService.send(chwPhone,
                    "Reminder: you have a pending patient assignment in your village. " +
                    "Open the app to review and accept within 24h.");
        }

        String chwFcmToken = chw.getUser().getFcmToken();
        if (chwFcmToken != null) {
            fcmService.sendGeneric(chwFcmToken,
                    "Pending Assignment Reminder",
                    "You still have a pending patient assignment in your village. Action required.",
                    "NEW_PATIENT_ASSIGNMENT");
        }

        log.info("Patient assignment 24h reminder sent: chw={}", chw.getId());
    }

    /** Fired by PatientAssignmentScheduler at the 48h mark — escalates to supervisor, full detail. */
    public void notifyPatientAssignmentEscalated(Patient patient, Chw chw) {
        alertService.createPatientAssignmentEscalatedAlert(patient, chw);
        log.info("Patient assignment escalated to supervisor: patient={} chw={}", patient.getId(), chw.getId());
    }

    private String protocolLabel(DiagnosisType type) {
        return switch (type) {
            case TB -> "TB_DOT_ADHERENCE";
            case HIV -> "ART_ADHERENCE";
            case HIV_TB_COINFECTION -> "ART_TB_DOT_ADHERENCE";
        };
    }
}

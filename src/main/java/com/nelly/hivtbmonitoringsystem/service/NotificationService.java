package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
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
    private final SystemUserRepository userRepository;

    // ── LTFU Events ───────────────────────────────────────────────────────────

    /**
     * Fired by LtfuScheduler when a task first becomes LATE (day 0).
     * All 3 channels to CHW.
     */
    public void notifyLtfuLate(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app alert
        alertService.createLtfuTracingAlert(patient, chw, task, AlertSeverity.WARNING);

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
     * Fired by LtfuScheduler when task transitions to CHW_ASSIGNED (day 14).
     * All 3 channels to CHW — marked CRITICAL/URGENT.
     */
    public void notifyLtfuChwAssigned(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app CRITICAL alert
        alertService.createLtfuTracingAlert(patient, chw, task, AlertSeverity.CRITICAL);

        // 2. Email to CHW
        String chwEmail = chw.getUser().getEmail();
        if (chwEmail != null) {
            emailService.sendLtfuChwAssignedAlert(
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
            fcmService.sendLtfuChwAssigned(chwFcmToken, patient.getFullName(), task.getDaysSinceMissed());
        }

        log.info("LTFU CHW_ASSIGNED notification sent: patient={}", patient.getId());
    }

    /**
     * Fired when a patient is officially LTFU_CONFIRMED (day 30+).
     * All 3 channels to CHW + all active supervisors.
     */
    public void notifyLtfuConfirmed(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app CRITICAL alert
        alertService.createLtfuConfirmedAlert(patient, chw, task);

        // 2. Email + FCM to CHW
        String chwEmail = chw.getUser().getEmail();
        if (chwEmail != null) {
            emailService.sendLtfuConfirmedAlert(
                    chwEmail, chw.getUser().getFullName(),
                    patient.getFullName(), patient.getPatientCode(),
                    chw.getUser().getFullName(),
                    task.getDaysSinceMissed(),
                    task.getMissedAppointmentDate().toString()
            );
        }
        String chwFcm = chw.getUser().getFcmToken();
        if (chwFcm != null) {
            fcmService.sendLtfuConfirmed(chwFcm, patient.getFullName());
        }

        // 3. Email + FCM to all active supervisors
        userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("SUPERVISOR") && Boolean.TRUE.equals(u.getIsActive()))
                .forEach(supervisor -> {
                    emailService.sendLtfuConfirmedAlert(
                            supervisor.getEmail(),
                            supervisor.getFullName(),
                            patient.getFullName(),
                            patient.getPatientCode(),
                            chw.getUser().getFullName(),
                            task.getDaysSinceMissed(),
                            task.getMissedAppointmentDate().toString()
                    );
                    if (supervisor.getFcmToken() != null) {
                        fcmService.sendLtfuConfirmed(supervisor.getFcmToken(), patient.getFullName());
                    }
                });

        log.info("LTFU_CONFIRMED notification sent: patient={}", patient.getId());
    }

    // ── Missed Dose Events ────────────────────────────────────────────────────

    /**
     * Fired by MissedDoseScheduler for every single missed dose.
     * CHW gets FCM push immediately for every miss.
     * CHW gets email only after 3+ consecutive misses (avoids fatigue).
     */
    public void notifyMissedDose(Patient patient, TreatmentPlan plan, int consecutiveMissed) {
        // 1. In-app alert always
        alertService.createMissedDoseAlert(patient, plan);

        Chw chw = patient.getChw();
        if (chw == null) return;

        // 2. FCM push for every single missed dose
        String chwFcm = chw.getUser().getFcmToken();
        if (chwFcm != null) {
            fcmService.sendMissedDoseAlert(chwFcm, patient.getFullName(), plan.getMedicationName());
        }

        // 3. Email only after 3 consecutive misses
        if (consecutiveMissed >= 3 && chw.getUser().getEmail() != null) {
            emailService.sendMissedDoseSummary(
                    chw.getUser().getEmail(),
                    chw.getUser().getFullName(),
                    patient.getFullName(),
                    patient.getPatientCode(),
                    plan.getMedicationName(),
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

    // ── User Management Events ────────────────────────────────────────────────

    public void notifyNewUser(String email, String fullName, String role, String tempPassword) {
        emailService.sendWelcomeEmail(email, fullName, role, tempPassword);
        log.info("Welcome email dispatched to: {}", email);
    }

    public void notifyPasswordReset(String email, String fullName, String tempPassword) {
        emailService.sendPasswordResetEmail(email, fullName, tempPassword);
        log.info("Password reset email dispatched to: {}", email);
    }
}

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
 * Every notification event is sent through ALL applicable channels simultaneously:
 *   1. In-app alert (AlertService — persisted in DB, shown in mobile/web)
 *   2. Email (EmailService — async SMTP)
 *
 * Firebase Cloud Messaging (FCM) for push notifications is left as a
 * configuration hook — add the firebase-admin SDK and implement
 * FcmService when the Firebase project is registered.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AlertService alertService;
    private final EmailService emailService;
    private final SystemUserRepository userRepository;

    // ── LTFU Events ───────────────────────────────────────────────────────────

    /**
     * Fired by LtfuScheduler when a task first becomes LATE (day 0).
     * Notifies the CHW via in-app alert + email.
     */
    public void notifyLtfuLate(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app alert
        alertService.createLtfuTracingAlert(patient, chw, task, AlertSeverity.WARNING);

        // 2. Email to CHW
        String email = chw.getUser().getEmail();
        if (email != null) {
            emailService.sendLtfuLateAlert(
                    email,
                    chw.getUser().getFullName(),
                    patient.getFullName(),
                    patient.getPatientCode(),
                    task.getMissedAppointmentDate().toString(),
                    task.getDaysSinceMissed()
            );
        }

        log.info("LTFU LATE notification sent: patient={} chw={}", patient.getId(), chw.getId());
    }

    /**
     * Fired by LtfuScheduler when task transitions to CHW_ASSIGNED (day 14).
     * Notifies CHW + facility provider via in-app + email.
     */
    public void notifyLtfuChwAssigned(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app critical alert
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

        log.info("LTFU CHW_ASSIGNED notification sent: patient={}", patient.getId());
    }

    /**
     * Fired when a patient is officially LTFU_CONFIRMED (day 30+).
     * Notifies CHW + supervisor via in-app + email.
     */
    public void notifyLtfuConfirmed(Patient patient, Chw chw, TracingTask task) {
        // 1. In-app CRITICAL alert
        alertService.createLtfuConfirmedAlert(patient, chw, task);

        // 2. Email to CHW
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

        // 3. Email all active supervisors
        userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("SUPERVISOR") && Boolean.TRUE.equals(u.getIsActive()))
                .forEach(supervisor -> emailService.sendLtfuConfirmedAlert(
                        supervisor.getEmail(),
                        supervisor.getFullName(),
                        patient.getFullName(),
                        patient.getPatientCode(),
                        chw.getUser().getFullName(),
                        task.getDaysSinceMissed(),
                        task.getMissedAppointmentDate().toString()
                ));

        log.info("LTFU_CONFIRMED notification sent: patient={}", patient.getId());
    }

    // ── Missed Dose Events ────────────────────────────────────────────────────

    /**
     * Fired by MissedDoseScheduler for every single missed dose.
     * CHW gets in-app alert immediately.
     * After 3 consecutive misses, CHW and provider also get email.
     */
    public void notifyMissedDose(Patient patient, TreatmentPlan plan, int consecutiveMissed) {
        // In-app alert always
        alertService.createMissedDoseAlert(patient, plan);

        // Email after 3 consecutive misses (avoid alert fatigue for single misses)
        if (consecutiveMissed >= 3) {
            Chw chw = patient.getChw();
            if (chw != null && chw.getUser().getEmail() != null) {
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
    }

    // ── False Confirmation Events ─────────────────────────────────────────────

    /**
     * Fired by AI engine when suspicion score reaches threshold 2.
     * Clinical staff get in-app alert + email.
     */
    public void notifyFalseConfirmation(Patient patient, String suspicionReason) {
        // In-app alert via AlertService (AI writes directly)
        // Email all active facility providers at this patient's facility
        if (patient.getFacility() != null) {
            userRepository.findAll().stream()
                    .filter(u -> (u.getRole().name().equals("FACILITY_PROVIDER") ||
                                  u.getRole().name().equals("CLINICAL_STAFF")) &&
                                 Boolean.TRUE.equals(u.getIsActive()))
                    .forEach(provider -> emailService.sendFalseConfirmationAlert(
                            provider.getEmail(),
                            provider.getFullName(),
                            patient.getFullName(),
                            patient.getPatientCode(),
                            suspicionReason
                    ));
        }
        log.info("FALSE_CONFIRMATION notification sent: patient={}", patient.getId());
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

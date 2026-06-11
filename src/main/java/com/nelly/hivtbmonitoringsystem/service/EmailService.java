package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Email notification service — Update 5 (thesis REQ-06).
 *
 * All sends are @Async so they never block the calling thread.
 * If mail is disabled (MAIL_ENABLED=false), all methods are no-ops.
 * All failures are caught and logged — never propagated to callers.
 *
 * Configuration via environment variables on Render:
 *   MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD,
 *   MAIL_FROM, MAIL_ENABLED
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@dreammedical.rw}")
    private String from;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMMM yyyy");

    // ── LTFU — Stage: LATE (day 0) ────────────────────────────────────────────

    @Async
    public void sendLtfuLateAlert(String toEmail, String chwName,
                                  String patientName, String patientCode,
                                  String missedDate, int daysSinceMissed) {
        if (!enabled) return;
        send(toEmail,
            "⚠️ LTFU Alert: Patient Missed Appointment — " + patientName,
            "Dear " + chwName + ",\n\n" +
            "Patient " + patientName + " (" + patientCode + ") missed their scheduled " +
            "facility appointment on " + missedDate + " (" + daysSinceMissed + " day(s) ago).\n\n" +
            "ACTION REQUIRED: Please make contact with this patient within the next 14 days.\n\n" +
            "If no contact is made within 14 days, this case will be automatically escalated " +
            "to an active CHW tracing assignment.\n\n" +
            "Patient details are available on your CHW mobile app under 'LTFU Tracing'.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── LTFU — Stage: CHW_ASSIGNED (day 14) ──────────────────────────────────

    @Async
    public void sendLtfuChwAssignedAlert(String toEmail, String chwName,
                                         String patientName, String patientCode,
                                         String village, int daysSinceMissed) {
        if (!enabled) return;
        send(toEmail,
            "🔴 URGENT LTFU Tracing Assignment — " + patientName,
            "Dear " + chwName + ",\n\n" +
            "URGENT: Patient " + patientName + " (" + patientCode + ") in " + village +
            " has had NO contact with the health facility for " + daysSinceMissed + " days.\n\n" +
            "This patient is now in ACTIVE TRACING status. You are required to:\n" +
            "1. Conduct an immediate home visit.\n" +
            "2. Record the tracing outcome in your CHW app.\n" +
            "3. Document the disengagement barrier and re-engagement plan.\n\n" +
            "If no contact is achieved within " + (30 - daysSinceMissed) + " more days, " +
            "this patient will be officially classified as LOST TO FOLLOW-UP per Rwanda " +
            "national protocol.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── LTFU — Stage: LTFU_CONFIRMED (day 30+) ───────────────────────────────

    @Async
    public void sendLtfuConfirmedAlert(String toEmail, String recipientName,
                                       String patientName, String patientCode,
                                       String chwName, int daysSinceMissed,
                                       String missedDate) {
        if (!enabled) return;
        send(toEmail,
            "🚨 LTFU CONFIRMED: " + patientName + " — Immediate Action Required",
            "Dear " + recipientName + ",\n\n" +
            "Patient " + patientName + " (" + patientCode + ") has been officially classified " +
            "as LOST TO FOLLOW-UP after " + daysSinceMissed + " consecutive days without " +
            "contact since their missed appointment on " + missedDate + ".\n\n" +
            "Assigned CHW: " + chwName + "\n\n" +
            "This case has been escalated to supervisor level per Rwanda national LTFU " +
            "protocol. Please review the full patient record and coordinate an immediate " +
            "clinical response.\n\n" +
            "The patient's tracing task details are available on the clinical dashboard.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── Missed dose alerts ────────────────────────────────────────────────────

    @Async
    public void sendMissedDoseSummary(String toEmail, String chwName,
                                      String patientName, String patientCode,
                                      String medicationName, int consecutiveMissed) {
        if (!enabled) return;
        send(toEmail,
            "⚠️ Missed Dose Alert: " + patientName + " — " + consecutiveMissed + " consecutive doses",
            "Dear " + chwName + ",\n\n" +
            "Patient " + patientName + " (" + patientCode + ") has missed " +
            consecutiveMissed + " consecutive doses of " + medicationName + ".\n\n" +
            "Please review their medication adherence and schedule a home visit " +
            "as soon as possible.\n\n" +
            "Full adherence history is available on your CHW mobile app.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── False confirmation alerts ─────────────────────────────────────────────

    @Async
    public void sendFalseConfirmationAlert(String toEmail, String providerName,
                                           String patientName, String patientCode,
                                           String suspicionReason) {
        if (!enabled) return;
        send(toEmail,
            "⚠️ Suspicious Confirmation Pattern — " + patientName,
            "Dear " + providerName + ",\n\n" +
            "The AI monitoring system has detected a suspicious medication confirmation " +
            "pattern for patient " + patientName + " (" + patientCode + ").\n\n" +
            "Reason: " + suspicionReason + "\n\n" +
            "Please review this patient's confirmation history and schedule a clinical " +
            "review with the assigned CHW to reconcile pill counts.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── Welcome / onboarding ──────────────────────────────────────────────────

    @Async
    public void sendWelcomeEmail(String toEmail, String fullName,
                                 String role, String tempPassword) {
        if (!enabled) return;
        send(toEmail,
            "Welcome to Dream Medical Center HIV/TB Monitor — " + fullName,
            "Dear " + fullName + ",\n\n" +
            "Your account has been created on the HIV/TB Patient Monitoring System " +
            "at Dream Medical Center, Rwanda.\n\n" +
            "Role: " + role + "\n" +
            "Email: " + toEmail + "\n" +
            "Temporary Password: " + tempPassword + "\n\n" +
            "You will be asked to change your password on your first login.\n\n" +
            "Access the system through:\n" +
            "  Mobile App: Download the HIV/TB Monitor app from your supervisor.\n" +
            "  Web Dashboard: https://hivtb-rw.onrender.com\n\n" +
            "If you have any issues, contact your System Administrator.\n\n" +
            "— Dream Medical Center");
    }

    // ── Password reset ────────────────────────────────────────────────────────

    @Async
    public void sendPasswordResetEmail(String toEmail, String fullName, String tempPassword) {
        if (!enabled) return;
        send(toEmail,
            "Your Password Has Been Reset — HIV/TB Monitor",
            "Dear " + fullName + ",\n\n" +
            "Your password on the HIV/TB Patient Monitoring System has been reset.\n\n" +
            "Temporary Password: " + tempPassword + "\n\n" +
            "You will be asked to set a new password on your next login.\n\n" +
            "If you did not request this reset, contact your System Administrator immediately.\n\n" +
            "— Dream Medical Center");
    }

    // ── Patient confirmed (Route B) ───────────────────────────────────────────

    @Async
    public void sendPatientConfirmedAlert(String toEmail, String chwName,
                                          String patientName, String referralId,
                                          String diagnosisName, String treatmentStartDate,
                                          String confirmedByName, String facilityName) {
        if (!enabled) return;
        send(toEmail,
            "Confirmed Patient Assigned — " + patientName,
            "Dear " + chwName + ",\n\n" +
            "A patient you referred has been confirmed and assigned to your care list.\n\n" +
            "Patient:          " + patientName + "\n" +
            "Referral ID:      " + referralId + "\n" +
            "Diagnosis:        " + diagnosisName + "\n" +
            "Treatment Start:  " + treatmentStartDate + "\n" +
            "Confirmed by:     " + confirmedByName + ", " + facilityName + "\n\n" +
            "ACTION REQUIRED:\n" +
            "Please conduct your first home visit within 24 hours to initiate " +
            "Directly Observed Therapy (DOTS).\n\n" +
            "Login to your app to view the patient's care plan.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── LTFU tracing resolved ─────────────────────────────────────────────────

    @Async
    public void sendTracingResolvedAlert(String toEmail, String providerName,
                                         String patientName, String patientCode,
                                         String chwName, String outcome, String resolutionPlan) {
        if (!enabled) return;
        send(toEmail,
            "Tracing Resolved — " + patientName,
            "Dear " + providerName + ",\n\n" +
            "CHW " + chwName + " has completed a tracing visit for patient " +
            patientName + " (" + patientCode + ").\n\n" +
            "Outcome: " + outcome + "\n" +
            (resolutionPlan != null && !resolutionPlan.isBlank()
                    ? "Re-engagement plan: " + resolutionPlan + "\n\n"
                    : "\n") +
            "The patient's AI risk score has been updated to reflect this outcome. " +
            "Please review the patient's record on the clinical dashboard.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── Alert escalation (48h unacknowledged) ─────────────────────────────────

    @Async
    public void sendAlertEscalation(String toEmail, String recipientName,
                                    String alertTitle, String alertMessage, int hoursOpen) {
        if (!enabled) return;
        send(toEmail,
            "🔺 Escalated Alert (unacknowledged " + hoursOpen + "h) — " + alertTitle,
            "Dear " + recipientName + ",\n\n" +
            "The following alert has not been acknowledged for over " + hoursOpen + " hours " +
            "and has been escalated to you for review:\n\n" +
            "Title:   " + alertTitle + "\n" +
            "Details: " + alertMessage + "\n\n" +
            "Please review and take action as soon as possible.\n\n" +
            "— Dream Medical Center HIV/TB Monitoring System");
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("[HIV/TB Monitor] " + subject);
            msg.setText(body);
            mailSender.send(msg);
            log.debug("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.warn("Email send failed to {}: {}", to, e.getMessage());
        }
    }
}

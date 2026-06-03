package com.nelly.hivtbmonitoringsystem.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Firebase Cloud Messaging push notification service — Update 5.
 *
 * All sends are @Async (non-blocking) and wrapped in try/catch so
 * FCM failures never crash the calling thread.
 *
 * If Firebase Admin SDK was not initialized (no service account JSON),
 * all methods return immediately as no-ops.
 */
@Service
@Slf4j
public class FcmService {

    private boolean isAvailable() {
        return !FirebaseApp.getApps().isEmpty();
    }

    // ── LTFU Notifications ────────────────────────────────────────────────────

    @Async
    public void sendLtfuLateAlert(String fcmToken, String patientName, int daysSinceMissed) {
        send(fcmToken,
                "⚠️ LTFU Alert: " + patientName,
                "Patient has been LATE for " + daysSinceMissed + " day(s). Make contact today.",
                "LTFU_LATE"
        );
    }

    @Async
    public void sendLtfuChwAssigned(String fcmToken, String patientName, int daysSinceMissed) {
        send(fcmToken,
                "🔴 URGENT Tracing: " + patientName,
                daysSinceMissed + " days without contact. Conduct home visit immediately.",
                "LTFU_CHW_ASSIGNED"
        );
    }

    @Async
    public void sendLtfuConfirmed(String fcmToken, String patientName) {
        send(fcmToken,
                "🚨 LTFU Confirmed: " + patientName,
                "Patient officially Lost to Follow-Up. Case escalated to supervisor.",
                "LTFU_CONFIRMED"
        );
    }

    // ── Missed Dose Notifications ─────────────────────────────────────────────

    @Async
    public void sendMissedDoseAlert(String fcmToken, String patientName, String medication) {
        send(fcmToken,
                "⚠️ Missed Dose: " + patientName,
                patientName + " did not confirm their " + medication + " dose.",
                "MISSED_DOSE"
        );
    }

    // ── False Confirmation Notifications ─────────────────────────────────────

    @Async
    public void sendFalseConfirmationAlert(String fcmToken, String patientName, String reason) {
        send(fcmToken,
                "⚠️ Suspicious Pattern: " + patientName,
                "AI flagged confirmation anomaly: " + reason,
                "FALSE_CONFIRMATION"
        );
    }

    // ── Patient confirmed (Route B) ───────────────────────────────────────────

    @Async
    public void sendPatientConfirmedAlert(String fcmToken, String patientName, String diagnosisName) {
        send(fcmToken,
                "✅ Patient Confirmed — Action Required",
                patientName + " you referred has been confirmed with " + diagnosisName +
                ". Assigned to your care list. Please initiate home visits.",
                "PATIENT_CONFIRMED"
        );
    }

    // ── Generic ───────────────────────────────────────────────────────────────

    @Async
    public void sendGeneric(String fcmToken, String title, String body, String type) {
        send(fcmToken, title, body, type);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void send(String token, String title, String body, String type) {
        if (!isAvailable()) return;
        if (token == null || token.isBlank()) {
            log.debug("FCM: no token for notification type={}", type);
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", type)
                    .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.debug("FCM sent: type={} messageId={}", type, response);
        } catch (FirebaseMessagingException e) {
            // Token expired/invalid — log but do not crash
            log.warn("FCM send failed: type={} error={}", type, e.getMessage());
        } catch (Exception e) {
            log.warn("FCM unexpected error: type={} error={}", type, e.getMessage());
        }
    }
}

package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.TestEmailRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.TestPushRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.TestSmsRequest;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.service.EmailService;
import com.nelly.hivtbmonitoringsystem.service.FcmService;
import com.nelly.hivtbmonitoringsystem.service.SmsOutboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin-only endpoints to fire test notifications without triggering real clinical events.
 * Useful for verifying email (SMTP) and push (FCM) configuration on new deployments.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
public class NotificationTestController {

    private final EmailService emailService;
    private final FcmService fcmService;
    private final SmsOutboundService smsOutboundService;
    private final SystemUserRepository userRepository;

    @PostMapping("/test-email")
    public ResponseEntity<Map<String, String>> testEmail(@Valid @RequestBody TestEmailRequest request) {
        String type = request.getNotificationType().toUpperCase();
        String patient = request.getPatientName() != null ? request.getPatientName() : "Test Patient";
        String code    = request.getPatientId()   != null ? request.getPatientId()   : "TEST-001";
        int days = request.getDaysSinceMissed() != null ? request.getDaysSinceMissed() : 0;

        switch (type) {
            case "LTFU_TRACING" -> emailService.sendLtfuLateAlert(
                    request.getRecipientEmail(),
                    "Test Recipient",
                    patient, code,
                    java.time.LocalDate.now().minusDays(days).toString(),
                    days
            );
            case "LTFU_CHW_ASSIGNED" -> emailService.sendLtfuChwAssignedAlert(
                    request.getRecipientEmail(),
                    "Test Recipient",
                    patient, code,
                    "Test Village",
                    days
            );
            case "LTFU_CONFIRMED" -> emailService.sendLtfuConfirmedAlert(
                    request.getRecipientEmail(),
                    "Test Recipient",
                    patient, code,
                    "Test CHW",
                    days,
                    java.time.LocalDate.now().minusDays(days).toString()
            );
            case "MISSED_DOSE" -> emailService.sendMissedDoseSummary(
                    request.getRecipientEmail(),
                    "Test CHW",
                    patient, code,
                    "TDF/3TC/EFV",
                    3
            );
            default -> emailService.sendWelcomeEmail(
                    request.getRecipientEmail(),
                    "Test User",
                    type,
                    "TestPass@2026"
            );
        }

        return ResponseEntity.ok(Map.of(
                "status", "dispatched",
                "type", type,
                "recipient", request.getRecipientEmail()
        ));
    }

    @PostMapping("/test-push")
    public ResponseEntity<Map<String, String>> testPush(@Valid @RequestBody TestPushRequest request) {
        SystemUser user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "status", "skipped",
                    "reason", "User has no registered FCM token",
                    "userId", request.getUserId().toString()
            ));
        }

        String type = (request.getData() != null && request.getData().containsKey("type"))
                ? request.getData().get("type")
                : "TEST";

        fcmService.sendGeneric(fcmToken, request.getTitle(), request.getBody(), type);

        return ResponseEntity.ok(Map.of(
                "status", "dispatched",
                "userId", request.getUserId().toString(),
                "recipient", user.getFullName()
        ));
    }

    @GetMapping("/sms-config")
    public ResponseEntity<Map<String, Object>> smsConfig() {
        return ResponseEntity.ok(smsOutboundService.getConfigStatus());
    }

    @PostMapping("/test-sms")
    public ResponseEntity<Map<String, Object>> testSms(@Valid @RequestBody TestSmsRequest request) {
        return ResponseEntity.ok(smsOutboundService.sendSync(request.getPhone(), request.getMessage()));
    }
}

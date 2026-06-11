package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.Alert;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.repository.AlertRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.service.EmailService;
import com.nelly.hivtbmonitoringsystem.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Runs hourly. Any unresolved alert that has not been acknowledged (is_read=false)
 * for more than 48 hours is escalated to all active supervisors via email + push,
 * and marked with escalated_at so it is not re-escalated.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertEscalationScheduler {

    private static final int ESCALATION_HOURS = 48;

    private final AlertRepository alertRepository;
    private final SystemUserRepository userRepository;
    private final EmailService emailService;
    private final FcmService fcmService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void escalateUnacknowledgedAlerts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(ESCALATION_HOURS);
        List<Alert> stale = alertRepository.findUnacknowledgedAlertsOlderThan(cutoff);
        if (stale.isEmpty()) return;

        List<SystemUser> supervisors = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("SUPERVISOR") && Boolean.TRUE.equals(u.getIsActive()))
                .toList();

        for (Alert alert : stale) {
            int hoursOpen = (int) ChronoUnit.HOURS.between(alert.getCreatedAt(), LocalDateTime.now());
            alert.setEscalatedAt(LocalDateTime.now());
            alert.setSeverity(AlertSeverity.CRITICAL);
            alertRepository.save(alert);

            for (SystemUser supervisor : supervisors) {
                emailService.sendAlertEscalation(supervisor.getEmail(), supervisor.getFullName(),
                        alert.getTitle(), alert.getMessage(), hoursOpen);
                if (supervisor.getFcmToken() != null) {
                    fcmService.sendGeneric(supervisor.getFcmToken(),
                            "Escalated Alert: " + alert.getTitle(), alert.getMessage(), "ALERT_ESCALATION");
                }
            }
            log.info("Alert escalated to supervisors: alertId={} hoursOpen={}", alert.getId(), hoursOpen);
        }
    }
}

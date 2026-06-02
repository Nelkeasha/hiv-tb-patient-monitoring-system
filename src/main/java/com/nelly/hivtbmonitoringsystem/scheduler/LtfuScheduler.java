package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.TracingTask;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.repository.TracingTaskRepository;
import com.nelly.hivtbmonitoringsystem.service.AlertService;
import com.nelly.hivtbmonitoringsystem.service.TracingTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Daily LTFU lifecycle manager.
 *
 * Per Rwanda national LTFU protocol and thesis REQ-14 / REQ-15:
 *   Day  0–13 → LATE     (patient missed appointment, CHW notified)
 *   Day 14–29 → CHW_ASSIGNED (escalate to active CHW tracing assignment)
 *   Day 30+   → LTFU_CONFIRMED (officially Lost to Follow-Up, supervisor escalated)
 *
 * Runs at 06:00 every day (before CHW morning priority list generation).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LtfuScheduler {

    private final TracingTaskRepository tracingTaskRepository;
    private final TracingTaskService tracingTaskService;
    private final AlertService alertService;

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void runDailyLtfuCycle() {
        log.info("LtfuScheduler: starting daily LTFU lifecycle run");
        List<TracingTask> activeTasks = tracingTaskRepository.findAllActive();
        int updated = 0;
        int escalated = 0;
        int confirmed = 0;

        for (TracingTask task : activeTasks) {
            // 1. Recalculate days_since_missed
            int days = (int) ChronoUnit.DAYS.between(task.getMissedAppointmentDate(), LocalDate.now());
            task.setDaysSinceMissed(Math.max(0, days));

            // 2. Stage transitions
            boolean changed = false;

            if (days >= 30 && !"LTFU_CONFIRMED".equals(task.getStatus())
                           && !"ESCALATED".equals(task.getStatus())
                           && !"RESOLVED".equals(task.getStatus())) {
                // Officially LTFU per Rwanda 30-day threshold
                task.setStatus("LTFU_CONFIRMED");
                task.setLtfuConfirmedAt(LocalDateTime.now());
                alertService.createLtfuConfirmedAlert(task.getPatient(), task.getChw(), task);
                confirmed++;
                changed = true;
                log.info("Patient LTFU confirmed: patient={} days={}", task.getPatient().getId(), days);

            } else if (days >= 14 && "LATE".equals(task.getStatus())) {
                // Escalate to CHW active tracing assignment
                task.setStatus("CHW_ASSIGNED");
                alertService.createLtfuTracingAlert(
                        task.getPatient(), task.getChw(), task, AlertSeverity.CRITICAL);
                escalated++;
                changed = true;
                log.info("Tracing task escalated to CHW_ASSIGNED: patient={} days={}",
                        task.getPatient().getId(), days);
            }

            if (changed || task.getDaysSinceMissed() != days) {
                tracingTaskRepository.save(task);
                updated++;
            }
        }

        log.info("LtfuScheduler complete: tasks_processed={} days_updated={} escalated_to_chw={} ltfu_confirmed={}",
                activeTasks.size(), updated, escalated, confirmed);
    }
}

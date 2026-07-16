package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.repository.ConfirmationLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.service.HomeVisitTaskService;
import com.nelly.hivtbmonitoringsystem.service.MedicationRecordService;
import com.nelly.hivtbmonitoringsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissedDoseScheduler {

    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final NotificationService notificationService;
    private final MedicationRecordService medicationRecordService;
    private final HomeVisitTaskService homeVisitTaskService;

    /**
     * Runs every minute. For each active dose schedule whose window has closed today,
     * auto-creates or updates the confirmation_log with is_missed=true if no confirmation
     * was recorded. This is the system's core proactive detection mechanism — missed doses
     * are flagged automatically without any CHW action required.
     */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void detectMissedDoses() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        List<DoseSchedule> activeSchedules = doseScheduleRepository.findByIsActiveTrue();

        for (DoseSchedule schedule : activeSchedules) {
            if (!schedule.isPlanActiveOn(today)) {
                continue; // plan not started yet, ended, or deactivated — nothing to miss today
            }
            LocalDateTime windowOpen  = today.atTime(schedule.getDoseTime());
            LocalDateTime windowClose = windowOpen.plusMinutes(schedule.getWindowDurationMinutes());

            if (now.isBefore(windowClose)) {
                continue; // window still open — too early to mark missed
            }

            Optional<ConfirmationLog> existing =
                    confirmationLogRepository.findByScheduleIdAndScheduledDate(schedule.getId(), today);

            if (existing.isEmpty()) {
                ConfirmationLog missed = ConfirmationLog.builder()
                        .patient(schedule.getPatient())
                        .plan(schedule.getPlan())
                        .schedule(schedule)
                        .scheduledDate(today)
                        .windowOpenTime(windowOpen)
                        .windowCloseTime(windowClose)
                        .confirmationMethod(schedule.getNotificationMethod())
                        .isWithinWindow(false)
                        .isMissed(true)
                        .aiSuspicionFlag(false)
                        .build();
                confirmationLogRepository.save(missed);
                int streak = consecutiveMissedStreak(schedule.getId());
                notificationService.notifyMissedDose(schedule.getPatient(), schedule.getPlan(), streak);
                if (streak >= 2) {
                    homeVisitTaskService.createTask(schedule.getPatient(),
                            HomeVisitTaskService.MISSED_DOSES, streak + " consecutive missed doses");
                }
                medicationRecordService.recalculate(schedule.getPatient().getId(), schedule.getPlan().getId(), today);
                log.info("Missed dose auto-recorded: patient={} schedule={}",
                        schedule.getPatient().getId(), schedule.getId());

            } else {
                ConfirmationLog entry = existing.get();
                if (entry.getConfirmedAt() == null && Boolean.FALSE.equals(entry.getIsMissed())) {
                    entry.setIsMissed(true);
                    confirmationLogRepository.save(entry);
                    int streak = consecutiveMissedStreak(schedule.getId());
                    notificationService.notifyMissedDose(schedule.getPatient(), schedule.getPlan(), streak);
                    if (streak >= 2) {
                        homeVisitTaskService.createTask(schedule.getPatient(),
                                HomeVisitTaskService.MISSED_DOSES, streak + " consecutive missed doses");
                    }
                    medicationRecordService.recalculate(schedule.getPatient().getId(), schedule.getPlan().getId(), today);
                }
            }
        }
    }

    /** Walks this dose schedule's log history backward from most recent, counting an unbroken run of misses. */
    private int consecutiveMissedStreak(java.util.UUID scheduleId) {
        int streak = 0;
        for (ConfirmationLog entry : confirmationLogRepository.findByScheduleIdOrderByScheduledDateDescCreatedAtDesc(scheduleId)) {
            if (!Boolean.TRUE.equals(entry.getIsMissed())) break;
            streak++;
        }
        return streak;
    }
}

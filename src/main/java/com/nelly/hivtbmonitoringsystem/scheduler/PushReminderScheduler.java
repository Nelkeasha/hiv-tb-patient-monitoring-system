package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.repository.ConfirmationLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Runs every minute. For each active APP-channel dose schedule, sends a push
 * reminder via FCM once the dose window has opened. The reminder fires anywhere
 * inside the window (not just the exact dose minute) so a missed scheduler tick
 * or brief instance sleep can't drop it, and {@code last_reminder_date} keeps it
 * to one reminder per schedule per day. Mirrors {@link SmsReminderScheduler}.
 *
 * <p>Uses the JVM default zone, which {@code TimeZoneConfig} pins to clinic-local
 * time — so an 08:00 dose reminds at 08:00 local, not 08:00 UTC.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PushReminderScheduler {

    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final FcmService fcmService;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void sendDoseReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        for (DoseSchedule schedule : doseScheduleRepository.findByIsActiveTrue()) {
            if (schedule.getNotificationMethod() != ConfirmationChannel.APP) {
                continue;
            }

            int window = schedule.getWindowDurationMinutes() != null ? schedule.getWindowDurationMinutes() : 45;
            LocalDateTime windowOpen  = today.atTime(schedule.getDoseTime());
            LocalDateTime windowClose = windowOpen.plusMinutes(window);
            if (now.isBefore(windowOpen) || !now.isBefore(windowClose)) {
                continue; // outside the send window (too early, or window already closed)
            }
            if (today.equals(schedule.getLastReminderDate())) {
                continue; // already reminded today
            }
            if (confirmationLogRepository.findByScheduleIdAndScheduledDate(schedule.getId(), today).isPresent()) {
                continue; // already confirmed/missed for today
            }

            String fcmToken = schedule.getPatient().getUser() != null
                    ? schedule.getPatient().getUser().getFcmToken() : null;
            if (fcmToken == null) {
                continue; // patient has no app account or never registered a device
            }

            String medication = schedule.getPlan() != null ? schedule.getPlan().getMedication().getName() : "your medication";
            String label = schedule.getDoseLabel() != null ? schedule.getDoseLabel() : "dose";

            fcmService.sendGeneric(
                    fcmToken,
                    "Medication Reminder",
                    "Time for your " + label + " (" + medication + "). Open the app to confirm.",
                    "DOSE_REMINDER");

            schedule.setLastReminderDate(today);
            doseScheduleRepository.save(schedule);
            log.info("Dose reminder push sent: patient={} schedule={}", schedule.getPatient().getId(), schedule.getId());
        }
    }
}

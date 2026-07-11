package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.repository.ConfirmationLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.service.SmsOutboundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Runs every minute. For each active SMS-channel dose schedule, sends a reminder
 * SMS asking the patient to reply YES/NO once they take their medication. The
 * reminder fires anywhere inside the dose window (not just the exact dose
 * minute) so a missed scheduler tick or brief instance sleep can't drop it, and
 * {@code last_reminder_date} keeps it to one reminder per schedule per day.
 *
 * <p>Uses the JVM default zone, which {@code TimeZoneConfig} pins to clinic-local
 * time — so an 08:00 dose reminds at 08:00 local, not 08:00 UTC.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsReminderScheduler {

    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final SmsOutboundService smsOutboundService;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void sendDoseReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        for (DoseSchedule schedule : doseScheduleRepository.findByIsActiveTrue()) {
            if (schedule.getNotificationMethod() != ConfirmationChannel.SMS) {
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

            String medication = schedule.getPlan() != null ? schedule.getPlan().getMedication().getName() : "your medication";
            String label = schedule.getDoseLabel() != null ? schedule.getDoseLabel() : "dose";
            String message = "Reminder: time for your " + label + " (" + medication + "). " +
                    "Reply YES once taken, or NO if not. - Dream Medical Center";

            smsOutboundService.send(schedule.getPatient().getPhoneNumber(), message);

            schedule.setLastReminderDate(today);
            doseScheduleRepository.save(schedule);
            log.info("Dose reminder SMS sent: patient={} schedule={}", schedule.getPatient().getId(), schedule.getId());
        }
    }
}

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
import java.time.LocalTime;

/**
 * Runs every minute. For each active APP-channel dose schedule whose dose time
 * falls within the current minute, sends a push reminder via FCM. Mirrors
 * SmsReminderScheduler exactly, but for app-channel patients instead of SMS.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PushReminderScheduler {

    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final FcmService fcmService;

    @Scheduled(fixedRate = 60_000)
    @Transactional(readOnly = true)
    public void sendDoseReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentMinute = now.toLocalTime().withSecond(0).withNano(0);

        for (DoseSchedule schedule : doseScheduleRepository.findByIsActiveTrue()) {
            if (schedule.getNotificationMethod() != ConfirmationChannel.APP) {
                continue;
            }
            if (!schedule.getDoseTime().withSecond(0).withNano(0).equals(currentMinute)) {
                continue;
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
            log.info("Dose reminder push sent: patient={} schedule={}", schedule.getPatient().getId(), schedule.getId());
        }
    }
}

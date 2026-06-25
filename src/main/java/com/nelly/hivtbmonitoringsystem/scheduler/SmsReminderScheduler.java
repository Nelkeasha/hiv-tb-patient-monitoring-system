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
import java.time.LocalTime;

/**
 * Runs every minute. For each active SMS-channel dose schedule whose dose time
 * falls within the current minute, sends a reminder SMS asking the patient to
 * reply YES/NO once they take their medication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsReminderScheduler {

    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final SmsOutboundService smsOutboundService;

    @Scheduled(fixedRate = 60_000)
    @Transactional(readOnly = true)
    public void sendDoseReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentMinute = now.toLocalTime().withSecond(0).withNano(0);

        for (DoseSchedule schedule : doseScheduleRepository.findByIsActiveTrue()) {
            if (schedule.getNotificationMethod() != ConfirmationChannel.SMS) {
                continue;
            }
            if (!schedule.getDoseTime().withSecond(0).withNano(0).equals(currentMinute)) {
                continue;
            }
            if (confirmationLogRepository.findByScheduleIdAndScheduledDate(schedule.getId(), today).isPresent()) {
                continue; // already confirmed/missed for today
            }

            String medication = schedule.getPlan() != null ? schedule.getPlan().getMedication().getName() : "your medication";
            String label = schedule.getDoseLabel() != null ? schedule.getDoseLabel() : "dose";
            String message = "Reminder: time for your " + label + " (" + medication + "). " +
                    "Reply YES once taken, or NO if not. - Dream Medical Center";

            smsOutboundService.send(schedule.getPatient().getPhoneNumber(), message);
            log.info("Dose reminder SMS sent: patient={} schedule={}", schedule.getPatient().getId(), schedule.getId());
        }
    }
}

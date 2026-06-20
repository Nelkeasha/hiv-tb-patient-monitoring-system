package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Hourly check for self-presented patients whose CHW has not yet accepted
 * the village-matched assignment (see PatientService#registerPatient):
 *
 *   24h unaccepted → SMS + push reminder to the CHW
 *   48h unaccepted → escalated to supervisor (full patient detail, CRITICAL alert)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatientAssignmentScheduler {

    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    private static final int REMINDER_HOURS = 24;
    private static final int ESCALATION_HOURS = 48;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void runHourlyCheck() {
        List<Patient> pending = patientRepository.findByChwAssignmentStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        int reminders = 0, escalations = 0;

        for (Patient patient : pending) {
            if (patient.getChwAssignedAt() == null) continue;
            long hours = ChronoUnit.HOURS.between(patient.getChwAssignedAt(), now);

            if (hours >= ESCALATION_HOURS && patient.getChwAssignmentEscalatedAt() == null) {
                notificationService.notifyPatientAssignmentEscalated(patient, patient.getChw());
                patient.setChwAssignmentEscalatedAt(now);
                patientRepository.save(patient);
                escalations++;
            } else if (hours >= REMINDER_HOURS && patient.getChwAssignmentReminderSentAt() == null) {
                notificationService.notifyPatientAssignmentReminder(patient.getChw());
                patient.setChwAssignmentReminderSentAt(now);
                patientRepository.save(patient);
                reminders++;
            }
        }

        if (reminders > 0 || escalations > 0) {
            log.info("PatientAssignmentScheduler: reminders={} escalations={}", reminders, escalations);
        }
    }
}

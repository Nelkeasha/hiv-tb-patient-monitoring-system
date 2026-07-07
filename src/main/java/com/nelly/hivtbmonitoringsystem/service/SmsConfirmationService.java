package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.ConfirmationLogResponse;
import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.repository.ConfirmationLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Handles inbound SMS callbacks from Africa's Talking.
 *
 * Africa's Talking posts form fields: from, text, to, date.
 * We look up the patient by phone number, find their active SMS schedule,
 * and record the confirmation or missed dose accordingly.
 *
 * Outbound reply SMS (for unrecognized text) is logged here but actual
 * delivery requires Africa's Talking SDK configured via app.sms.enabled.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsConfirmationService {

    private final PatientRepository patientRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final AlertService alertService;

    public enum SmsResult { CONFIRMED, MISSED, UNRECOGNIZED, PATIENT_NOT_FOUND, NO_ACTIVE_SCHEDULE, ALREADY_CONFIRMED }

    @Transactional
    public SmsResult process(String fromPhone, String text) {
        String normalizedPhone = normalizePhone(fromPhone);
        String reply = text == null ? "" : text.trim().toUpperCase();

        Optional<Patient> patientOpt = patientRepository.findByPhoneNumber(normalizedPhone);
        if (patientOpt.isEmpty()) {
            patientOpt = patientRepository.findByPhoneNumber(fromPhone);
        }

        if (patientOpt.isEmpty()) {
            log.info("SMS callback: no patient found for phone={}", fromPhone);
            return SmsResult.PATIENT_NOT_FOUND;
        }

        Patient patient = patientOpt.get();
        List<DoseSchedule> schedules = doseScheduleRepository.findByPatientIdAndIsActiveTrue(patient.getId());
        Optional<DoseSchedule> smsSchedule = schedules.stream()
                .filter(s -> s.getNotificationMethod() == ConfirmationChannel.SMS)
                .findFirst();

        if (smsSchedule.isEmpty()) {
            log.info("SMS callback: no active SMS schedule for patient={}", patient.getId());
            return SmsResult.NO_ACTIVE_SCHEDULE;
        }

        DoseSchedule schedule = smsSchedule.get();
        LocalDate today = LocalDate.now();

        Optional<ConfirmationLog> existing = confirmationLogRepository.findByScheduleIdAndScheduledDate(
                schedule.getId(), today);

        if (existing.isPresent() && existing.get().getConfirmedAt() != null) {
            log.info("SMS callback: dose already confirmed today for patient={}", patient.getId());
            return SmsResult.ALREADY_CONFIRMED;
        }

        if (existing.isPresent() && Boolean.TRUE.equals(existing.get().getIsMissed())
                && existing.get().getConfirmedAt() == null) {
            log.info("SMS callback: dose already recorded as missed (window closed) for patient={}", patient.getId());
            return SmsResult.MISSED;
        }

        if ("YES".equals(reply) || "OUI".equals(reply) || "YEGO".equals(reply)) {
            return recordConfirmed(patient, schedule, today, reply);
        } else if ("NO".equals(reply) || "NON".equals(reply) || "OYA".equals(reply)) {
            return recordMissed(patient, schedule, today, reply);
        } else {
            log.info("SMS callback: unrecognized reply '{}' from phone={}", text, fromPhone);
            return SmsResult.UNRECOGNIZED;
        }
    }

    private SmsResult recordConfirmed(Patient patient, DoseSchedule schedule, LocalDate today, String rawReply) {
        LocalDateTime windowOpen  = today.atTime(schedule.getDoseTime());
        LocalDateTime windowClose = windowOpen.plusMinutes(schedule.getWindowDurationMinutes());
        LocalDateTime confirmedAt = LocalDateTime.now();

        boolean isWithinWindow = !confirmedAt.isBefore(windowOpen) && !confirmedAt.isAfter(windowClose);
        if (!isWithinWindow) {
            // A "YES" arriving after the confirmation window has closed is still a missed dose.
            return recordMissed(patient, schedule, today, rawReply);
        }

        int responseTimeSec = (int) Duration.between(windowOpen, confirmedAt).abs().getSeconds();

        ConfirmationLog entry = ConfirmationLog.builder()
                .patient(patient)
                .plan(schedule.getPlan())
                .schedule(schedule)
                .scheduledDate(today)
                .windowOpenTime(windowOpen)
                .windowCloseTime(windowClose)
                .confirmedAt(confirmedAt)
                .responseTimeSeconds(responseTimeSec)
                .confirmationMethod(ConfirmationChannel.SMS)
                .rawSmsResponse(rawReply)
                .isWithinWindow(true)
                .isMissed(false)
                .aiSuspicionFlag(false)
                .build();

        confirmationLogRepository.save(entry);
        auditLogService.log("SMS_CONFIRMATION", "confirmation_logs", entry.getId());
        // Condition cleared: a confirmed dose ends the streak, so auto-resolve any
        // open MISSED_DOSE alert for this patient (mirrors the app confirmation path).
        alertService.autoResolvePatientAlerts(patient.getId(), AlertType.MISSED_DOSE);
        log.info("SMS callback: dose confirmed for patient={} withinWindow=true", patient.getId());
        return SmsResult.CONFIRMED;
    }

    private SmsResult recordMissed(Patient patient, DoseSchedule schedule, LocalDate today, String rawReply) {
        LocalDateTime windowOpen  = today.atTime(schedule.getDoseTime());
        LocalDateTime windowClose = windowOpen.plusMinutes(schedule.getWindowDurationMinutes());

        ConfirmationLog entry = ConfirmationLog.builder()
                .patient(patient)
                .plan(schedule.getPlan())
                .schedule(schedule)
                .scheduledDate(today)
                .windowOpenTime(windowOpen)
                .windowCloseTime(windowClose)
                .confirmedAt(null)
                .responseTimeSeconds(null)
                .confirmationMethod(ConfirmationChannel.SMS)
                .rawSmsResponse(rawReply)
                .isWithinWindow(false)
                .isMissed(true)
                .aiSuspicionFlag(false)
                .build();

        confirmationLogRepository.save(entry);
        auditLogService.log("SMS_MISSED_DOSE", "confirmation_logs", entry.getId());
        notificationService.notifyMissedDose(patient, schedule.getPlan(), consecutiveMissedStreak(schedule.getId()));
        log.info("SMS callback: missed dose recorded for patient={}", patient.getId());
        return SmsResult.MISSED;
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

    private String normalizePhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("250") && digits.length() == 12) return "+" + digits;
        if (digits.startsWith("07") && digits.length() == 10) return "+250" + digits.substring(1);
        return phone;
    }
}

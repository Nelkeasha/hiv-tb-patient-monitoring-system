package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import com.nelly.hivtbmonitoringsystem.entity.MedicationRecord;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.TreatmentPlan;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.repository.ConfirmationLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.repository.HomeVisitRepository;
import com.nelly.hivtbmonitoringsystem.repository.MedicationRecordRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.TreatmentPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Re-derives one MedicationRecord row per (patient, plan, day) fresh from
 * source data (confirmation_logs, dose_schedules, home_visits) every call —
 * never increments counters, so a retried or duplicate call is always safe.
 * Feeds the facility/supervisor/admin adherence reports and the nightly
 * clinical correlation job.
 */
@Service
@RequiredArgsConstructor
public class MedicationRecordService {

    private static final BigDecimal BELOW_THRESHOLD_PCT = BigDecimal.valueOf(80);

    private final MedicationRecordRepository medicationRecordRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final PatientRepository patientRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;

    @Transactional
    public void recalculate(UUID patientId, UUID planId, LocalDate day) {
        if (planId == null) return; // nothing to scope the record to

        Patient patient = patientRepository.findById(patientId).orElse(null);
        TreatmentPlan plan = treatmentPlanRepository.findById(planId).orElse(null);
        if (patient == null || plan == null) return;

        int dosesScheduled = doseScheduleRepository.findByPlanIdAndIsActiveTrue(planId).size();
        if (dosesScheduled == 0) return; // no active schedule for this plan yet — nothing to record

        List<ConfirmationLog> dayLogs = confirmationLogRepository
                .findByPatientIdAndScheduledDate(patientId, day).stream()
                .filter(log -> log.getPlan() != null && log.getPlan().getId().equals(planId))
                .toList();

        int dosesConfirmed = (int) dayLogs.stream().filter(l -> l.getConfirmedAt() != null).count();
        boolean falseConfirmationFlag = dayLogs.stream().anyMatch(l -> Boolean.TRUE.equals(l.getAiSuspicionFlag()));

        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        boolean pillDiscrepancyToday = homeVisitRepository
                .findByPatientIdAndVisitDateBetween(patientId, dayStart, dayEnd).stream()
                .anyMatch(v -> Boolean.TRUE.equals(v.getPillCountDiscrepancy()));
        int dosesVerified = pillDiscrepancyToday ? 0 : dosesConfirmed;

        BigDecimal adherencePct = BigDecimal.valueOf(dosesConfirmed)
                .divide(BigDecimal.valueOf(dosesScheduled), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        boolean belowThreshold = adherencePct.compareTo(BELOW_THRESHOLD_PCT) < 0;

        MedicationRecord record = medicationRecordRepository
                .findByPatientIdAndPlanIdAndPeriodStart(patientId, planId, day)
                .orElseGet(() -> MedicationRecord.builder()
                        .patient(patient)
                        .plan(plan)
                        .periodStart(day)
                        .periodEnd(day)
                        .syncStatus(SyncStatus.PENDING)
                        .build());

        record.setDosesScheduled(dosesScheduled);
        record.setDosesConfirmed(dosesConfirmed);
        record.setDosesVerified(dosesVerified);
        record.setAdherencePct(adherencePct);
        record.setBelowThreshold(belowThreshold);
        record.setFalseConfirmationFlag(falseConfirmationFlag);

        medicationRecordRepository.save(record);
    }
}

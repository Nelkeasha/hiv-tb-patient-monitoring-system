package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.service.AlertService;
import com.nelly.hivtbmonitoringsystem.service.HomeVisitTaskService;
import com.nelly.hivtbmonitoringsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Daily LTFU lifecycle manager — runs at 06:00 every day.
 *
 * Two phases per run:
 *
 * PHASE 1 — Auto-detect missed appointments (creates NEW tracing tasks):
 *   Source A: Confirmed referrals where facility_appointment_date has passed
 *             but status is still CONFIRMED or MODIFIED (patient never attended).
 *   Source B: Active patients with no home visit in the last 28 days
 *             (standard monthly ART/TB refill cycle gap).
 *
 * PHASE 2 — Lifecycle progression (updates EXISTING active tasks):
 *   Day  0–14 → LATE                 — initial flag, CHW notified
 *   Day 15–29 → IIT_ESCALATED        — active tracing required, CRITICAL alert
 *   Day 30+   → TREATMENT_INTERRUPTED — officially LTFU per Rwanda national protocol,
 *                                       supervisor escalated
 *
 * All notification channels (in-app, email, FCM) fired via NotificationService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LtfuScheduler {

    private final TracingTaskRepository tracingTaskRepository;
    private final ReferralRepository referralRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final PatientRepository patientRepository;
    private final AlertService alertService;
    private final NotificationService notificationService;
    private final HomeVisitTaskService homeVisitTaskService;

    /** Monthly ART/TB refill cycle — patient should be seen at least every 28 days. */
    private static final int VISIT_GAP_DAYS = 28;

    /**
     * Rwanda-MOH administrative/cohort classification thresholds — independent of
     * the 14/30-day operational clock above. This is the reporting-cohort clock
     * used for national ART/TB program statistics (WHO/PEPFAR-aligned): a patient
     * isn't counted as administratively lost until 90 days without contact, and
     * isn't definitively LOST_TO_FOLLOW_UP for cohort-reporting purposes until 12
     * months. The operational status (LATE/IIT_ESCALATED/TREATMENT_INTERRUPTED)
     * drives day-to-day CHW/clinical action; this drives quarterly/annual cohort
     * reporting and can lag well behind it.
     */
    private static final int ADMIN_LATE_DAYS = 90;
    private static final int ADMIN_LTFU_DAYS = 365;

    private String administrativeClassification(int daysSinceMissed) {
        if (daysSinceMissed >= ADMIN_LTFU_DAYS) return "LOST_TO_FOLLOW_UP";
        if (daysSinceMissed >= ADMIN_LATE_DAYS)  return "LATE";
        return "ON_TIME";
    }

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void runDailyLtfuCycle() {
        log.info("LtfuScheduler: starting daily LTFU cycle");

        int created = detectMissedAppointments();
        int[] results = progressExistingTasks();

        log.info("LtfuScheduler complete — new_tasks={} updated={} escalated={} ltfu_confirmed={}",
                created, results[0], results[1], results[2]);
    }

    // ── Phase 1 — Auto-detect missed appointments ─────────────────────────────

    private int detectMissedAppointments() {
        LocalDate today = LocalDate.now();
        int created = 0;

        // Source A: referrals with a confirmed appointment date that has now passed
        created += detectFromMissedReferrals(today);

        // Source B: active patients with no home visit in the last 28 days
        created += detectFromVisitGap(today);

        return created;
    }

    /**
     * Source A — Referral-based detection.
     *
     * A referral is CONFIRMED with a facility_appointment_date set by the provider.
     * If that date has now passed and the status is still CONFIRMED (not ATTENDED
     * or NOT_ATTENDED), the patient missed their appointment.
     */
    private int detectFromMissedReferrals(LocalDate today) {
        List<Referral> missed = referralRepository.findMissedAppointments(today);
        int created = 0;

        for (Referral referral : missed) {
            Patient patient = referral.getPatient();
            LocalDate appointmentDate = referral.getFacilityAppointmentDate();
            Chw chw = patient.getChw();

            if (chw == null || !Boolean.TRUE.equals(patient.getIsActive())) continue;

            // Skip if an open tracing task already exists for this patient + date
            if (tracingTaskRepository.findOpenTaskByPatientAndDate(
                    patient.getId(), appointmentDate).isPresent()) {
                continue;
            }

            long daysSince = ChronoUnit.DAYS.between(appointmentDate, today);

            TracingTask task = TracingTask.builder()
                    .patient(patient)
                    .chw(chw)
                    .missedAppointmentDate(appointmentDate)
                    .daysSinceMissed((int) daysSince)
                    .reason("MISSED_APPOINTMENT")
                    .status("LATE")
                    .administrativeClassification(administrativeClassification((int) daysSince))
                    .proxyAuthorized(false)
                    .build();

            tracingTaskRepository.save(task);
            notificationService.notifyLtfuLate(patient, chw, task);
            created++;

            log.info("Tracing task auto-created (missed referral appointment): patient={} date={} days={}",
                    patient.getId(), appointmentDate, daysSince);
        }

        return created;
    }

    /**
     * Source B — Visit-gap detection.
     *
     * Active patients with no recorded home visit in the last 28 days have
     * likely missed their monthly medication refill. We flag them as LATE
     * using today − 28 days as the implied missed appointment date.
     *
     * Patients already covered by Source A (referral-based) are excluded
     * via the open-task uniqueness check.
     */
    private int detectFromVisitGap(LocalDate today) {
        LocalDateTime cutoff = today.minusDays(VISIT_GAP_DAYS).atStartOfDay();
        List<UUID> gapPatientIds = homeVisitRepository.findPatientIdsWithNoVisitSince(cutoff);

        // Also include active patients who have NEVER had a home visit
        // and whose treatment started more than 28 days ago
        List<Patient> allActive = patientRepository.findAllByIsActiveTrue();
        Set<UUID> visitedSet = gapPatientIds.stream().collect(Collectors.toSet());

        // Add patients with no visits at all (never visited)
        allActive.stream()
                .filter(p -> homeVisitRepository.countByPatientIdAndVisitDateAfter(
                        p.getId(), today.minusYears(10).atStartOfDay()) == 0)
                .filter(p -> p.getArtStartDate() != null &&
                        ChronoUnit.DAYS.between(p.getArtStartDate(), today) > VISIT_GAP_DAYS)
                .map(Patient::getId)
                .forEach(visitedSet::add);

        int created = 0;
        LocalDate impliedMissedDate = today.minusDays(VISIT_GAP_DAYS);

        for (UUID patientId : visitedSet) {
            Patient patient = patientRepository.findById(patientId).orElse(null);
            if (patient == null || !Boolean.TRUE.equals(patient.getIsActive())) continue;

            Chw chw = patient.getChw();
            if (chw == null) continue;

            // Skip if open task already exists for this patient + implied date
            if (tracingTaskRepository.findOpenTaskByPatientAndDate(
                    patient.getId(), impliedMissedDate).isPresent()) {
                continue;
            }

            // Also skip if any open task exists within the last 28 days
            // (avoid duplicate tasks for same patient)
            boolean hasRecentOpenTask = tracingTaskRepository.findAllActive().stream()
                    .anyMatch(t -> t.getPatient().getId().equals(patientId) &&
                              ChronoUnit.DAYS.between(t.getMissedAppointmentDate(), today) <= VISIT_GAP_DAYS);
            if (hasRecentOpenTask) continue;

            long daysSince = VISIT_GAP_DAYS;

            TracingTask task = TracingTask.builder()
                    .patient(patient)
                    .chw(chw)
                    .missedAppointmentDate(impliedMissedDate)
                    .daysSinceMissed((int) daysSince)
                    .reason("MISSED_REFILL")
                    .status("LATE")
                    .administrativeClassification(administrativeClassification((int) daysSince))
                    .proxyAuthorized(false)
                    .build();

            tracingTaskRepository.save(task);
            notificationService.notifyLtfuLate(patient, chw, task);
            created++;

            log.info("Tracing task auto-created (visit gap): patient={} implied_date={} days={}",
                    patientId, impliedMissedDate, daysSince);
        }

        return created;
    }

    // ── Phase 2 — Progress existing tasks through lifecycle ───────────────────

    private int[] progressExistingTasks() {
        LocalDate today = LocalDate.now();
        List<TracingTask> activeTasks = tracingTaskRepository.findAllActive();
        int updated = 0, escalated = 0, confirmed = 0;

        for (TracingTask task : activeTasks) {
            int days = (int) ChronoUnit.DAYS.between(task.getMissedAppointmentDate(), today);
            int clampedDays = Math.max(0, days);
            // Capture before mutating — comparing the getter to itself after
            // the setter already ran would always be equal, silently making
            // the "did this actually change" check below permanently false.
            boolean changed = !java.util.Objects.equals(task.getDaysSinceMissed(), clampedDays);
            task.setDaysSinceMissed(clampedDays);

            String newAdminClass = administrativeClassification(clampedDays);
            if (!newAdminClass.equals(task.getAdministrativeClassification())) {
                task.setAdministrativeClassification(newAdminClass);
                changed = true;
            }

            if (days >= 30
                    && !"TREATMENT_INTERRUPTED".equals(task.getStatus())
                    && !"ESCALATED".equals(task.getStatus())
                    && !"RESOLVED".equals(task.getStatus())) {

                task.setStatus("TREATMENT_INTERRUPTED");
                task.setLtfuConfirmedAt(LocalDateTime.now());
                notificationService.notifyTreatmentInterrupted(task.getPatient(), task.getChw(), task);
                confirmed++;
                changed = true;
                log.info("Patient treatment interrupted (LTFU confirmed): patient={} days={}", task.getPatient().getId(), days);

            } else if (days >= 15 && "LATE".equals(task.getStatus())) {

                task.setStatus("IIT_ESCALATED");
                notificationService.notifyIitEscalated(task.getPatient(), task.getChw(), task);
                homeVisitTaskService.createTask(task.getPatient(), HomeVisitTaskService.IIT_ESCALATED,
                        "Treatment-interruption tracing escalated (" + days + " days late)");
                escalated++;
                changed = true;
                log.info("Tracing task → IIT_ESCALATED: patient={} days={}", task.getPatient().getId(), days);
            }

            if (changed) {
                tracingTaskRepository.save(task);
                updated++;
            }
        }

        return new int[]{updated, escalated, confirmed};
    }
}

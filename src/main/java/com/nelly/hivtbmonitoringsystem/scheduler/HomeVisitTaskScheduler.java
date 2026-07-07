package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.AiRiskScore;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.repository.AiRiskScoreRepository;
import com.nelly.hivtbmonitoringsystem.repository.HomeVisitRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.service.HomeVisitTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Generates the two triggered home-visit tasks that aren't tied to a single
 * event: HIGH_RISK (patient's latest AI score is HIGH/CRITICAL) and
 * PERIODIC_REVIEW (no in-person visit within the review interval). Runs every
 * 6 hours; {@link HomeVisitTaskService#createTask} keeps it idempotent.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HomeVisitTaskScheduler {

    /** Configurable review cadence — default monthly (not daily). */
    private static final int PERIODIC_REVIEW_DAYS = 30;

    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final PatientRepository patientRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final HomeVisitTaskService homeVisitTaskService;

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void generateRiskAndReviewTasks() {
        int highRisk = 0, periodic = 0;

        // HIGH_RISK — latest score per patient that is HIGH or CRITICAL.
        for (AiRiskScore score : aiRiskScoreRepository.findLatestHighRiskScores()) {
            Patient p = score.getPatient();
            if (p == null || Boolean.FALSE.equals(p.getIsActive())) continue;
            homeVisitTaskService.createTask(p, HomeVisitTaskService.HIGH_RISK,
                    "AI risk level " + score.getRiskLevel());
            highRisk++;
        }

        // PERIODIC_REVIEW — active confirmed patients with no recent in-person visit.
        // Use a COUNT-since-cutoff query rather than loading each patient's full
        // visit history just to read the latest date (was an N+1).
        LocalDateTime cutoff = LocalDate.now().minusDays(PERIODIC_REVIEW_DAYS).atStartOfDay();
        for (Patient p : patientRepository.findByIsActiveTrueAndRegistrationStatus("CONFIRMED")) {
            boolean dueForReview = homeVisitRepository.countByPatientIdAndVisitDateAfter(p.getId(), cutoff) == 0;
            if (dueForReview) {
                homeVisitTaskService.createTask(p, HomeVisitTaskService.PERIODIC_REVIEW,
                        "Scheduled periodic in-person review (every " + PERIODIC_REVIEW_DAYS + " days)");
                periodic++;
            }
        }

        if (highRisk > 0 || periodic > 0) {
            log.info("HomeVisitTaskScheduler — high_risk_tasks={} periodic_review_tasks={}", highRisk, periodic);
        }
    }
}

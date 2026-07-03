package com.nelly.hivtbmonitoringsystem.scheduler;

import com.nelly.hivtbmonitoringsystem.entity.FhirSyncLog;
import com.nelly.hivtbmonitoringsystem.repository.FhirSyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reaps orphaned FHIR sync sessions. POST /api/chw/sync/trigger opens a
 * FhirSyncLog in IN_PROGRESS and hands the id to the external sync script; if
 * that script is never run (or crashes before completing), the session would
 * otherwise stay IN_PROGRESS forever. Every 15 minutes this marks any session
 * still IN_PROGRESS after {@link #STALE_MINUTES} as FAILED.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FhirSyncReaperScheduler {

    private static final int STALE_MINUTES = 30;

    private final FhirSyncLogRepository fhirSyncLogRepository;

    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void reapStaleSyncSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(STALE_MINUTES);
        List<FhirSyncLog> stale =
                fhirSyncLogRepository.findBySyncStatusAndSyncStartedAtBefore("IN_PROGRESS", cutoff);
        if (stale.isEmpty()) return;

        for (FhirSyncLog session : stale) {
            session.setSyncStatus("FAILED");
            session.setSyncCompletedAt(LocalDateTime.now());
            String prev = session.getErrorLog() == null ? "" : session.getErrorLog() + "\n";
            session.setErrorLog(prev + "session timed out — sync never completed within "
                    + STALE_MINUTES + " minutes");
            fhirSyncLogRepository.save(session);
        }
        log.warn("Reaped {} stale FHIR sync session(s) (IN_PROGRESS > {} min)",
                stale.size(), STALE_MINUTES);
    }
}

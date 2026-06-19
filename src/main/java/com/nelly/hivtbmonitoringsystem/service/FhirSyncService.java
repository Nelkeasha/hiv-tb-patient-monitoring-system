package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.CompleteSyncRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FhirSyncService {

    private final FhirSyncLogRepository fhirSyncLogRepository;
    private final PatientRepository patientRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final FacilityProviderRepository facilityProviderRepository;
    private final SystemUserRepository systemUserRepository;
    private final ChwRepository chwRepository;

    // ── CHW-facing ────────────────────────────────────────────────────────────

    /** ADMIN/SYSTEM_ADMIN have no Chw profile — they get a system-wide view instead of "my own". */
    public SyncPendingResponse getPendingCounts() {
        SystemUser user = resolveCurrentUser();
        if (isAdmin(user)) return buildPendingResponseSystemWide();
        Chw chw = resolveChw(user);
        return buildPendingResponse(chw.getId());
    }

    @Transactional
    public SyncTriggerResponse triggerSync() {
        SystemUser user = resolveCurrentUser();
        if (isAdmin(user)) return triggerSyncSystemWide();

        Chw chw = resolveChw(user);
        UUID chwId = chw.getId();

        int pp  = patientRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        int phv = homeVisitRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        int pmr = medicationRecordRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        int ptp = treatmentPlanRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();

        FhirSyncLog log = FhirSyncLog.builder()
                .chw(chw)
                .syncStartedAt(LocalDateTime.now())
                .recordsSynced(0)
                .recordsFailed(0)
                .syncStatus("IN_PROGRESS")
                .build();
        log = fhirSyncLogRepository.save(log);

        return SyncTriggerResponse.builder()
                .logId(log.getId())
                .syncStatus("IN_PROGRESS")
                .pendingPatients(pp)
                .pendingHomeVisits(phv)
                .pendingMedicationRecords(pmr)
                .pendingTreatmentPlans(ptp)
                .totalQueued(pp + phv + pmr + ptp)
                .syncStartedAt(log.getSyncStartedAt())
                .build();
    }

    public List<FhirSyncLogResponse> getSyncHistory() {
        SystemUser user = resolveCurrentUser();
        if (isAdmin(user)) {
            return fhirSyncLogRepository.findAllByOrderBySyncStartedAtDesc()
                    .stream().map(this::toLogResponse).toList();
        }
        Chw chw = resolveChw(user);
        return fhirSyncLogRepository.findByChwIdOrderBySyncStartedAtDesc(chw.getId())
                .stream().map(this::toLogResponse).toList();
    }

    public FhirSyncLogResponse getSyncLog(UUID logId) {
        SystemUser user = resolveCurrentUser();
        FhirSyncLog log = fhirSyncLogRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sync log not found"));
        if (!isAdmin(user)) {
            Chw chw = resolveChw(user);
            if (log.getChw() == null || !log.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
        }
        return toLogResponse(log);
    }

    // ── Internal — called by external FHIR service (SYSTEM_ADMIN) ────────────

    @Transactional
    public FhirSyncLogResponse completeSyncLog(UUID logId, CompleteSyncRequest req) {
        FhirSyncLog log = fhirSyncLogRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sync log not found"));

        applyFhirIds(req.getPatientFhirIds(), req.getHomeVisitFhirIds(),
                req.getMedicationRecordFhirIds(), req.getTreatmentPlanFhirIds());

        log.setSyncStatus(req.getSyncStatus());
        log.setRecordsSynced(req.getRecordsSynced());
        log.setRecordsFailed(req.getRecordsFailed());
        log.setSyncCompletedAt(LocalDateTime.now());
        if (req.getErrorLog() != null) {
            log.setErrorLog(req.getErrorLog());
        }

        return toLogResponse(fhirSyncLogRepository.save(log));
    }

    // ── Facility provider — read-only overview ────────────────────────────────

    public FacilitySyncStatusResponse getFacilitySyncStatus() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        FacilityProvider provider = facilityProviderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Facility provider profile not found"));

        UUID facilityId = provider.getFacility().getId();

        long pp  = patientRepository.countByFacilityIdAndSyncStatus(facilityId, SyncStatus.PENDING);
        long phv = homeVisitRepository.countByFacilityIdAndSyncStatus(facilityId, SyncStatus.PENDING);
        long pmr = medicationRecordRepository.countByFacilityIdAndSyncStatus(facilityId, SyncStatus.PENDING);
        long ptp = treatmentPlanRepository.countByFacilityIdAndSyncStatus(facilityId, SyncStatus.PENDING);

        return FacilitySyncStatusResponse.builder()
                .facilityName(provider.getFacility().getName())
                .pendingPatients(pp)
                .pendingHomeVisits(phv)
                .pendingMedicationRecords(pmr)
                .pendingTreatmentPlans(ptp)
                .total(pp + phv + pmr + ptp)
                .lastSyncCompletedAt(
                        fhirSyncLogRepository.findLastCompletedSyncForFacility(facilityId).orElse(null))
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyFhirIds(Map<String, String> patientIds,
                               Map<String, String> visitIds,
                               Map<String, String> recordIds,
                               Map<String, String> planIds) {
        if (patientIds != null) {
            patientIds.forEach((idStr, fhirId) -> {
                patientRepository.findById(UUID.fromString(idStr)).ifPresent(p -> {
                    p.setFhirPatientId(fhirId);
                    p.setSyncStatus(SyncStatus.SYNCED);
                    patientRepository.save(p);
                });
            });
        }
        if (visitIds != null) {
            visitIds.forEach((idStr, fhirId) -> {
                homeVisitRepository.findById(UUID.fromString(idStr)).ifPresent(v -> {
                    v.setFhirObservationId(fhirId);
                    v.setSyncStatus(SyncStatus.SYNCED);
                    homeVisitRepository.save(v);
                });
            });
        }
        if (recordIds != null) {
            recordIds.forEach((idStr, fhirId) -> {
                medicationRecordRepository.findById(Long.parseLong(idStr)).ifPresent(r -> {
                    r.setFhirStatementId(fhirId);
                    r.setSyncStatus(SyncStatus.SYNCED);
                    medicationRecordRepository.save(r);
                });
            });
        }
        if (planIds != null) {
            planIds.forEach((idStr, fhirId) -> {
                treatmentPlanRepository.findById(UUID.fromString(idStr)).ifPresent(tp -> {
                    tp.setFhirCarePlanId(fhirId);
                    tp.setSyncStatus(SyncStatus.SYNCED);
                    treatmentPlanRepository.save(tp);
                });
            });
        }
    }

    private SyncPendingResponse buildPendingResponse(UUID chwId) {
        int pp  = patientRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        int phv = homeVisitRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        int pmr = medicationRecordRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        int ptp = treatmentPlanRepository.findByChwIdAndSyncStatus(chwId, SyncStatus.PENDING).size();
        return SyncPendingResponse.builder()
                .pendingPatients(pp)
                .pendingHomeVisits(phv)
                .pendingMedicationRecords(pmr)
                .pendingTreatmentPlans(ptp)
                .total(pp + phv + pmr + ptp)
                .build();
    }

    private SyncPendingResponse buildPendingResponseSystemWide() {
        long pp  = patientRepository.countBySyncStatus(SyncStatus.PENDING);
        long phv = homeVisitRepository.countBySyncStatus(SyncStatus.PENDING);
        long pmr = medicationRecordRepository.countBySyncStatus(SyncStatus.PENDING);
        long ptp = treatmentPlanRepository.countBySyncStatus(SyncStatus.PENDING);
        return SyncPendingResponse.builder()
                .pendingPatients((int) pp)
                .pendingHomeVisits((int) phv)
                .pendingMedicationRecords((int) pmr)
                .pendingTreatmentPlans((int) ptp)
                .total((int) (pp + phv + pmr + ptp))
                .build();
    }

    private SyncTriggerResponse triggerSyncSystemWide() {
        long pp  = patientRepository.countBySyncStatus(SyncStatus.PENDING);
        long phv = homeVisitRepository.countBySyncStatus(SyncStatus.PENDING);
        long pmr = medicationRecordRepository.countBySyncStatus(SyncStatus.PENDING);
        long ptp = treatmentPlanRepository.countBySyncStatus(SyncStatus.PENDING);

        FhirSyncLog log = FhirSyncLog.builder()
                .chw(null)
                .syncStartedAt(LocalDateTime.now())
                .recordsSynced(0)
                .recordsFailed(0)
                .syncStatus("IN_PROGRESS")
                .build();
        log = fhirSyncLogRepository.save(log);

        return SyncTriggerResponse.builder()
                .logId(log.getId())
                .syncStatus("IN_PROGRESS")
                .pendingPatients((int) pp)
                .pendingHomeVisits((int) phv)
                .pendingMedicationRecords((int) pmr)
                .pendingTreatmentPlans((int) ptp)
                .totalQueued((int) (pp + phv + pmr + ptp))
                .syncStartedAt(log.getSyncStartedAt())
                .build();
    }

    private boolean isAdmin(SystemUser user) {
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SYSTEM_ADMIN;
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Chw resolveChw(SystemUser user) {
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "CHW profile not found"));
    }

    private FhirSyncLogResponse toLogResponse(FhirSyncLog l) {
        return FhirSyncLogResponse.builder()
                .id(l.getId())
                .chwId(l.getChw() != null ? l.getChw().getId() : null)
                .chwName(l.getChw() != null ? l.getChw().getUser().getFullName() : null)
                .syncStatus(l.getSyncStatus())
                .recordsSynced(l.getRecordsSynced() != null ? l.getRecordsSynced() : 0)
                .recordsFailed(l.getRecordsFailed() != null ? l.getRecordsFailed() : 0)
                .syncStartedAt(l.getSyncStartedAt())
                .syncCompletedAt(l.getSyncCompletedAt())
                .errorLog(l.getErrorLog())
                .createdAt(l.getCreatedAt())
                .build();
    }
}

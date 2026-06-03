package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.CompleteSyncRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.service.FhirSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FhirSyncController {

    private final FhirSyncService fhirSyncService;

    // ── CHW — sync management ─────────────────────────────────────────────────

    /** How many records of each type are waiting to be synced to FHIR. */
    @GetMapping("/chw/sync/pending")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<SyncPendingResponse> getPendingCounts() {
        return ResponseEntity.ok(fhirSyncService.getPendingCounts());
    }

    /**
     * Opens a new sync session. Returns the log ID and pending counts so the
     * external FHIR service knows exactly what to process. Session starts as
     * IN_PROGRESS until the FHIR service calls the complete endpoint.
     */
    @PostMapping("/chw/sync/trigger")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<SyncTriggerResponse> triggerSync() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fhirSyncService.triggerSync());
    }

    /** History of all sync sessions for this CHW, most recent first. */
    @GetMapping("/chw/sync/history")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<FhirSyncLogResponse>> getSyncHistory() {
        return ResponseEntity.ok(fhirSyncService.getSyncHistory());
    }

    /** Detail of a single sync session. */
    @GetMapping("/chw/sync/history/{logId}")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<FhirSyncLogResponse> getSyncLog(@PathVariable UUID logId) {
        return ResponseEntity.ok(fhirSyncService.getSyncLog(logId));
    }

    // ── Internal — called by the external FHIR service ────────────────────────

    /**
     * Closes a sync session. The external FHIR service provides the outcome
     * (records synced/failed) and the FHIR resource IDs assigned during sync.
     * Spring Boot stores the FHIR IDs on each entity and marks them SYNCED.
     */
    @PutMapping("/internal/sync/logs/{logId}/complete")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<FhirSyncLogResponse> completeSyncLog(
            @PathVariable UUID logId,
            @Valid @RequestBody CompleteSyncRequest request) {
        return ResponseEntity.ok(fhirSyncService.completeSyncLog(logId, request));
    }

    // ── Facility provider — read-only overview ────────────────────────────────

    /** Total pending records across all resource types at the provider's facility. */
    @GetMapping("/clinical/sync/status")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<FacilitySyncStatusResponse> getFacilitySyncStatus() {
        return ResponseEntity.ok(fhirSyncService.getFacilitySyncStatus());
    }
}

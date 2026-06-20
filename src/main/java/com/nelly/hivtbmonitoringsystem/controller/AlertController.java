package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.CreateAlertRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ReportSyncFailureRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AlertResponse;
import com.nelly.hivtbmonitoringsystem.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // ── CHW ──────────────────────────────────────────────────────────────────

    /** Unresolved alerts assigned to the logged-in CHW, newest first. */
    @GetMapping("/chw")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<AlertResponse>> getChwAlerts() {
        return ResponseEntity.ok(alertService.getChwAlerts());
    }

    /** Unresolved alerts for a specific patient (CHW must own that patient). */
    @GetMapping("/chw/patient/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<AlertResponse>> getChwPatientAlerts(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(alertService.getChwPatientAlerts(patientId));
    }

    // ── Clinical / facility provider ─────────────────────────────────────────

    /** Unresolved CRITICAL and WARNING alerts across the system. */
    @GetMapping("/clinical")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<AlertResponse>> getClinicalAlerts() {
        return ResponseEntity.ok(alertService.getClinicalAlerts());
    }

    /** All unresolved alerts for a specific patient — clinical staff view. */
    @GetMapping("/clinical/patient/{patientId}")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<AlertResponse>> getClinicalPatientAlerts(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(alertService.getClinicalPatientAlerts(patientId));
    }

    // ── State mutations ───────────────────────────────────────────────────────

    /** Mark an alert as read. CHW can only mark their own alerts. */
    @PutMapping("/{alertId}/read")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<AlertResponse> markRead(@PathVariable UUID alertId) {
        return ResponseEntity.ok(alertService.markRead(alertId));
    }

    /** Mark an alert as resolved. CHW can only resolve their own alerts. */
    @PutMapping("/{alertId}/resolve")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<AlertResponse> markResolved(@PathVariable UUID alertId) {
        return ResponseEntity.ok(alertService.markResolved(alertId));
    }

    // ── Offline sync queue (CHW / patient mobile app) ─────────────────────────

    /** Reports an offline-queued action (home visit, dose confirmation) that the
     *  server permanently rejected — surfaces as a SYNC_FAILURE alert. */
    @PostMapping("/sync-failure")
    @PreAuthorize("hasAnyRole('CHW', 'PATIENT')")
    public ResponseEntity<AlertResponse> reportSyncFailure(
            @Valid @RequestBody ReportSyncFailureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.reportSyncFailure(request));
    }

    // ── Internal — AI microservice ────────────────────────────────────────────

    /** AI microservice creates a new alert (uses SYSTEM_ADMIN credentials). */
    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<AlertResponse> createAlert(
            @Valid @RequestBody CreateAlertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.createAlert(request));
    }
}

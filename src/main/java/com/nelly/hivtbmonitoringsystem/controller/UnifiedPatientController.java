package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.ConfirmPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ResolveNegativeRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RegisterPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ScreenPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.ProvisionalPatientResponse;
import com.nelly.hivtbmonitoringsystem.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Unified patient API — /api/v1/patients/
 *
 * Route A  POST /register     CLINICAL_STAFF only → ACTIVE record
 * Route B  POST /screen       CHW only            → PROVISIONAL record + referral ID
 *          PUT  /{id}/confirm  CLINICAL_STAFF only → PROVISIONAL → ACTIVE, notifies CHW
 *
 * Read
 *   GET /my              CHW — own patients only (JWT-inferred)
 *   GET /{id}            CHW (own), CLINICAL_STAFF, SUPERVISOR, ADMIN
 *   GET /chw/{chwId}     SUPERVISOR, CLINICAL_STAFF, ADMIN
 *   GET /provisional     CLINICAL_STAFF, ADMIN
 */
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class UnifiedPatientController {

    private final PatientService patientService;

    // ── Route B — CHW provisional screening ──────────────────────────────────

    @PostMapping("/screen")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ProvisionalPatientResponse> screen(
            @Valid @RequestBody ScreenPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.screenPatient(request));
    }

    // ── Route A — Clinical staff facility registration ────────────────────────

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<PatientResponse> register(
            @Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.registerPatient(request));
    }

    // ── Route B confirmation ──────────────────────────────────────────────────

    @PutMapping("/{patientId}/confirm")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<PatientResponse> confirm(
            @PathVariable UUID patientId,
            @Valid @RequestBody ConfirmPatientRequest request) {
        return ResponseEntity.ok(patientService.confirmPatient(patientId, request));
    }

    /** Route B negative resolution — lab result came back negative; flag the voucher
     *  RESOLVED_NEGATIVE (registry block) and redirect to prevention (RBC 2022). */
    @PutMapping("/{patientId}/resolve-negative")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<PatientResponse> resolveNegative(
            @PathVariable UUID patientId,
            @Valid @RequestBody ResolveNegativeRequest request) {
        return ResponseEntity.ok(patientService.resolveNegative(patientId, request));
    }

    // ── Reads ─────────────────────────────────────────────────────────────────

    /** CHW's own patients — inferred from JWT, no chwId param needed. */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<PatientResponse>> getMyPatients() {
        return ResponseEntity.ok(patientService.getMyPatients());
    }

    /** Single patient — CHW sees own only; clinical/supervisor see any. */
    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(patientService.getPatientForAnyRole(patientId));
    }

    /** All patients assigned to a specific CHW — used by supervisors and clinical staff. */
    @GetMapping("/chw/{chwId}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<PatientResponse>> getPatientsForChw(@PathVariable UUID chwId) {
        return ResponseEntity.ok(patientService.getPatientsForChw(chwId));
    }

    /** All PROVISIONAL patients awaiting clinical confirmation. */
    @GetMapping("/provisional")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<PatientResponse>> getProvisional() {
        return ResponseEntity.ok(patientService.getProvisionalPatients());
    }
}

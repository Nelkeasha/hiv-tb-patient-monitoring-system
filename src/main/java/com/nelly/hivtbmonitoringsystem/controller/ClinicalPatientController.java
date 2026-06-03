package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.ConfirmPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RegisterPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
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
 * Clinical staff patient management endpoints.
 *
 * Route A — register a confirmed patient directly at the facility.
 * Route B confirmation — upgrade a CHW-screened provisional patient to active.
 */
@RestController
@RequestMapping("/api/clinical/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
public class ClinicalPatientController {

    private final PatientService patientService;

    /** Route A — clinical staff registers a confirmed patient (status: ACTIVE). */
    @PostMapping
    public ResponseEntity<PatientResponse> register(
            @Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.registerPatient(request));
    }

    /**
     * Route B confirmation — clinical staff upgrades a provisional patient to active.
     * Notifies the CHW who performed the original screening.
     */
    @PutMapping("/{patientId}/confirm")
    public ResponseEntity<PatientResponse> confirm(
            @PathVariable UUID patientId,
            @Valid @RequestBody ConfirmPatientRequest request) {
        return ResponseEntity.ok(patientService.confirmPatient(patientId, request));
    }

    /** All PROVISIONAL patients waiting for clinical confirmation. */
    @GetMapping("/provisional")
    public ResponseEntity<List<PatientResponse>> getProvisional() {
        return ResponseEntity.ok(patientService.getProvisionalPatients());
    }
}

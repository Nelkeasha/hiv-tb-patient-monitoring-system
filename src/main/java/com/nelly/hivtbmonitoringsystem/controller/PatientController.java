package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.UpdatePatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.PendingAssignmentResponse;
import com.nelly.hivtbmonitoringsystem.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Flutter backward-compat read endpoints.
 * All patient write operations have moved to /api/v1/patients/.
 */
@RestController
@RequestMapping("/api/chw/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getMyPatients() {
        return ResponseEntity.ok(patientService.getMyPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    // ── Village → CHW assignment acceptance (self-presented facility patients) ─

    /** Masked list — name/diagnosis withheld until accepted via the endpoint below. */
    @GetMapping("/pending-assignments")
    public ResponseEntity<List<PendingAssignmentResponse>> getPendingAssignments() {
        return ResponseEntity.ok(patientService.getPendingAssignments());
    }

    @PostMapping("/{id}/accept-assignment")
    public ResponseEntity<PatientResponse> acceptAssignment(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.acceptAssignment(id));
    }
}

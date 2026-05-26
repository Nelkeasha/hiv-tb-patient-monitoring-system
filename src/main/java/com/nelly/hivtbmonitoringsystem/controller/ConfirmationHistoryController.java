package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.ConfirmationLogResponse;
import com.nelly.hivtbmonitoringsystem.service.PatientConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * On-demand confirmation log queries for CHW dashboard, clinical staff,
 * and the AI microservice (uses SYSTEM_ADMIN credentials).
 */
@RestController
@RequestMapping("/api/confirmations")
@RequiredArgsConstructor
public class ConfirmationHistoryController {

    private final PatientConfirmationService confirmationService;

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ConfirmationLogResponse>> getPatientHistory(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(confirmationService.getPatientHistory(patientId));
    }

    @GetMapping("/patient/{patientId}/missed")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ConfirmationLogResponse>> getPatientMissedDoses(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(confirmationService.getPatientMissedDoses(patientId));
    }
}

package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.WriteAiRiskScoreRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AiRiskScoreResponse;
import com.nelly.hivtbmonitoringsystem.service.AiRiskScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/risk-scores")
@RequiredArgsConstructor
public class AiRiskScoreController {

    private final AiRiskScoreService riskScoreService;

    /** CHW morning priority list — own patients sorted highest risk first. */
    @GetMapping("/chw/priority-list")
    @PreAuthorize("hasRole('CHW')")
    public ResponseEntity<List<AiRiskScoreResponse>> getChwPriorityList() {
        return ResponseEntity.ok(riskScoreService.getChwPriorityList());
    }

    /** Latest risk score for a specific patient. */
    @GetMapping("/patient/{patientId}/latest")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'SYSTEM_ADMIN')")
    public ResponseEntity<AiRiskScoreResponse> getLatestForPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(riskScoreService.getLatestForPatient(patientId));
    }

    /** Full score history for a patient, most recent first. */
    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<AiRiskScoreResponse>> getHistoryForPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(riskScoreService.getHistoryForPatient(patientId));
    }

    /** Facility dashboard — all HIGH and CRITICAL patients, highest risk first. */
    @GetMapping("/high-risk")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<AiRiskScoreResponse>> getHighRiskPatients() {
        return ResponseEntity.ok(riskScoreService.getHighRiskPatients());
    }

    /** Patient's own latest risk score. */
    @GetMapping("/me/latest")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AiRiskScoreResponse> getMyLatestScore() {
        return ResponseEntity.ok(riskScoreService.getMyLatestScore());
    }

    /** AI microservice writes a new score (uses SYSTEM_ADMIN credentials). */
    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<AiRiskScoreResponse> writeScore(
            @Valid @RequestBody WriteAiRiskScoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(riskScoreService.writeScore(request));
    }
}

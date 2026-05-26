package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.SubmitConfirmationRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.ConfirmationLogResponse;
import com.nelly.hivtbmonitoringsystem.service.PatientConfirmationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/confirmations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientConfirmationController {

    private final PatientConfirmationService confirmationService;

    @PostMapping
    public ResponseEntity<ConfirmationLogResponse> submit(
            @Valid @RequestBody SubmitConfirmationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(confirmationService.submitConfirmation(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ConfirmationLogResponse>> getHistory() {
        return ResponseEntity.ok(confirmationService.getMyHistory());
    }
}

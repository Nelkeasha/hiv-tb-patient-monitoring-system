package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.EnrollPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdatePatientRequest;
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

@RestController
@RequestMapping("/api/chw/patients")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHW')")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> enroll(@Valid @RequestBody EnrollPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.enrollPatient(request));
    }

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
                                                   @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }
}

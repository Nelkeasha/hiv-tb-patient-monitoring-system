package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.EnrollPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ScreenPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdatePatientRequest;
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

@RestController
@RequestMapping("/api/chw/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
public class PatientController {

    private final PatientService patientService;

    /**
     * Route B — CHW creates a provisional screening record in the field.
     * Returns a referral ID the patient presents at the health center.
     */
    @PostMapping("/screen")
    public ResponseEntity<ProvisionalPatientResponse> screen(
            @Valid @RequestBody ScreenPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.screenPatient(request));
    }

    /**
     * Legacy enroll endpoint — kept for Flutter backward compatibility.
     * Now creates a PROVISIONAL record identical to /screen.
     */
    @PostMapping
    public ResponseEntity<PatientResponse> enroll(
            @Valid @RequestBody EnrollPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.enrollPatient(request));
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

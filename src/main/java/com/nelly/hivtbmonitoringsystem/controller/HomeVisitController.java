package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.RecordVisitRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateHomeVisitRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.HomeVisitResponse;
import com.nelly.hivtbmonitoringsystem.service.HomeVisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chw/visits")
@RequiredArgsConstructor
public class HomeVisitController {

    private final HomeVisitService homeVisitService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<HomeVisitResponse> recordVisit(@Valid @RequestBody RecordVisitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(homeVisitService.recordVisit(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<HomeVisitResponse> updateVisit(@PathVariable UUID id,
                                                          @Valid @RequestBody UpdateHomeVisitRequest request) {
        return ResponseEntity.ok(homeVisitService.updateVisit(id, request));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<HomeVisitResponse>> getVisitsForPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(homeVisitService.getVisitsForPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/latest")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<HomeVisitResponse> getLatestForPatient(@PathVariable UUID patientId) {
        return homeVisitService.getLatestForPatient(patientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chw/{chwId}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<HomeVisitResponse>> getByChw(@PathVariable UUID chwId) {
        return ResponseEntity.ok(homeVisitService.getVisitsForChw(chwId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<HomeVisitResponse> getVisit(@PathVariable UUID id) {
        return ResponseEntity.ok(homeVisitService.getVisit(id));
    }
}

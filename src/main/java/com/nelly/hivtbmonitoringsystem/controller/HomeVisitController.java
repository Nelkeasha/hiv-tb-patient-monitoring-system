package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.RecordVisitRequest;
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
@PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
public class HomeVisitController {

    private final HomeVisitService homeVisitService;

    @PostMapping
    public ResponseEntity<HomeVisitResponse> recordVisit(@Valid @RequestBody RecordVisitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(homeVisitService.recordVisit(request));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<HomeVisitResponse>> getVisitsForPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(homeVisitService.getVisitsForPatient(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeVisitResponse> getVisit(@PathVariable UUID id) {
        return ResponseEntity.ok(homeVisitService.getVisit(id));
    }
}

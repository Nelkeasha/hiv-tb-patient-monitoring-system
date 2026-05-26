package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.AddDoseScheduleRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateTreatmentPlanRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateTreatmentPlanRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.DoseScheduleResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.TreatmentPlanResponse;
import com.nelly.hivtbmonitoringsystem.service.TreatmentPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chw/treatment-plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHW')")
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    @PostMapping
    public ResponseEntity<TreatmentPlanResponse> createPlan(
            @Valid @RequestBody CreateTreatmentPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(treatmentPlanService.createPlan(request));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TreatmentPlanResponse>> getPatientPlans(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(treatmentPlanService.getPatientPlans(patientId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<TreatmentPlanResponse> getPlan(@PathVariable UUID planId) {
        return ResponseEntity.ok(treatmentPlanService.getPlan(planId));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<TreatmentPlanResponse> updatePlan(
            @PathVariable UUID planId,
            @RequestBody UpdateTreatmentPlanRequest request) {
        return ResponseEntity.ok(treatmentPlanService.updatePlan(planId, request));
    }

    @PostMapping("/{planId}/schedules")
    public ResponseEntity<DoseScheduleResponse> addSchedule(
            @PathVariable UUID planId,
            @Valid @RequestBody AddDoseScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(treatmentPlanService.addSchedule(planId, request));
    }

    @GetMapping("/{planId}/schedules")
    public ResponseEntity<List<DoseScheduleResponse>> getSchedules(@PathVariable UUID planId) {
        return ResponseEntity.ok(treatmentPlanService.getSchedules(planId));
    }

    @PutMapping("/schedules/{scheduleId}/deactivate")
    public ResponseEntity<DoseScheduleResponse> deactivateSchedule(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(treatmentPlanService.deactivateSchedule(scheduleId));
    }
}

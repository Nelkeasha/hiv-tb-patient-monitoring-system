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

/**
 * Treatment plan and dose schedule management.
 *
 * Write access (create / update / deactivate) is restricted to CLINICAL_STAFF
 * and FACILITY_PROVIDER — CHWs have read-only access.
 */
@RestController
@RequestMapping("/api/treatment-plans")
@RequiredArgsConstructor
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    // ── Clinical staff — write ────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<TreatmentPlanResponse> createPlan(
            @Valid @RequestBody CreateTreatmentPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(treatmentPlanService.createPlan(request));
    }

    @PutMapping("/{planId}")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<TreatmentPlanResponse> updatePlan(
            @PathVariable UUID planId,
            @RequestBody UpdateTreatmentPlanRequest request) {
        return ResponseEntity.ok(treatmentPlanService.updatePlan(planId, request));
    }

    @PostMapping("/{planId}/schedules")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<DoseScheduleResponse> addSchedule(
            @PathVariable UUID planId,
            @Valid @RequestBody AddDoseScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(treatmentPlanService.addSchedule(planId, request));
    }

    @PutMapping("/schedules/{scheduleId}/deactivate")
    @PreAuthorize("hasAnyRole('CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<DoseScheduleResponse> deactivateSchedule(@PathVariable UUID scheduleId) {
        return ResponseEntity.ok(treatmentPlanService.deactivateSchedule(scheduleId));
    }

    // ── Shared read — clinical staff + CHW (CHW sees own patients only) ───────

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<TreatmentPlanResponse>> getPatientPlans(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(treatmentPlanService.getPatientPlans(patientId));
    }

    @GetMapping("/{planId}")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<TreatmentPlanResponse> getPlan(@PathVariable UUID planId) {
        return ResponseEntity.ok(treatmentPlanService.getPlan(planId));
    }

    @GetMapping("/{planId}/schedules")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<DoseScheduleResponse>> getSchedules(@PathVariable UUID planId) {
        return ResponseEntity.ok(treatmentPlanService.getSchedules(planId));
    }

    /** All active schedules for a patient — used by CHW during home visits. */
    @GetMapping("/patient/{patientId}/schedules/active")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<DoseScheduleResponse>> getActiveSchedulesForPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(treatmentPlanService.getActiveSchedulesForPatient(patientId));
    }
}

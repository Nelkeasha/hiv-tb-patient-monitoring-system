package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.GenerateTracingTaskRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ResolveTracingTaskRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateTracingStatusRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.TracingTaskResponse;
import com.nelly.hivtbmonitoringsystem.service.TracingTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * LTFU Tracing Task endpoints.
 * Implements Update 2 — REQ-14 and REQ-15 from thesis.
 */
@RestController
@RequestMapping("/api/tracing")
@RequiredArgsConstructor
public class TracingTaskController {

    private final TracingTaskService tracingTaskService;

    // ── System / Admin: generate task ────────────────────────────────────────

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN', 'FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SUPERVISOR')")
    public ResponseEntity<TracingTaskResponse> generateTask(
            @Valid @RequestBody GenerateTracingTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tracingTaskService.generateTracingTask(request));
    }

    // ── CHW: update task stage ────────────────────────────────────────────────

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasAnyRole('CHW', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<TracingTaskResponse> updateStatus(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTracingStatusRequest request) {
        return ResponseEntity.ok(tracingTaskService.updateTracingStatus(taskId, request));
    }

    // ── CHW: record tracing visit outcome ─────────────────────────────────────

    @PutMapping("/{taskId}/resolve")
    @PreAuthorize("hasAnyRole('CHW', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<TracingTaskResponse> resolve(
            @PathVariable UUID taskId,
            @Valid @RequestBody ResolveTracingTaskRequest request) {
        return ResponseEntity.ok(tracingTaskService.resolveTracingTask(taskId, request));
    }

    // ── CHW: escalate to supervisor ───────────────────────────────────────────

    @PutMapping("/{taskId}/escalate")
    @PreAuthorize("hasAnyRole('CHW', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<TracingTaskResponse> escalate(@PathVariable UUID taskId) {
        return ResponseEntity.ok(tracingTaskService.escalateToSupervisor(taskId));
    }

    // ── CHW: view my daily tracing tasks ─────────────────────────────────────

    @GetMapping("/chw/my-tasks")
    @PreAuthorize("hasAnyRole('CHW', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<TracingTaskResponse>> getMyTasks() {
        return ResponseEntity.ok(tracingTaskService.getDailyTracingTasksForCurrentChw());
    }

    @GetMapping("/chw/{chwId}")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<TracingTaskResponse>> getChwTasks(@PathVariable UUID chwId) {
        return ResponseEntity.ok(tracingTaskService.getDailyTracingTasks(chwId));
    }

    // ── Any clinical user: patient tracing history ────────────────────────────

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SUPERVISOR', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<TracingTaskResponse>> getPatientHistory(@PathVariable UUID patientId) {
        return ResponseEntity.ok(tracingTaskService.getPatientTracingHistory(patientId));
    }

    // ── Supervisor: escalated cases ───────────────────────────────────────────

    @GetMapping("/supervisor/escalated")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<TracingTaskResponse>> getEscalated() {
        return ResponseEntity.ok(tracingTaskService.getEscalatedTasks());
    }

    @GetMapping("/supervisor/ltfu-confirmed")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'FACILITY_PROVIDER', 'CLINICAL_STAFF', 'SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<TracingTaskResponse>> getLtfuConfirmed() {
        return ResponseEntity.ok(tracingTaskService.getLtfuConfirmedTasks());
    }
}

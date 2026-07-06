package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.HomeVisitTaskResponse;
import com.nelly.hivtbmonitoringsystem.service.HomeVisitTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Triggered home-visit tasks. A CHW sees their own open tasks; clinical staff
 * and supervisors/admins see their facility's (the "triggered home visits" list).
 */
@RestController
@RequestMapping("/api/home-visit-tasks")
@RequiredArgsConstructor
public class HomeVisitTaskController {

    private final HomeVisitTaskService homeVisitTaskService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<HomeVisitTaskResponse>> getOpenTasks() {
        return ResponseEntity.ok(homeVisitTaskService.getOpenTasksForCaller());
    }

    @PutMapping("/{taskId}/complete")
    @PreAuthorize("hasAnyRole('CHW', 'CLINICAL_STAFF', 'FACILITY_PROVIDER', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<Void> complete(@PathVariable UUID taskId) {
        homeVisitTaskService.completeTask(taskId);
        return ResponseEntity.noContent().build();
    }
}

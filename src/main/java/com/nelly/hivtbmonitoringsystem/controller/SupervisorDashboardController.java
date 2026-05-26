package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.service.SupervisorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERVISOR')")
public class SupervisorDashboardController {

    private final SupervisorDashboardService dashboardService;

    /** Operational overview: CHW counts, risk distribution, visit activity, missed dose totals. */
    @GetMapping("/stats")
    public ResponseEntity<SupervisorStatsResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    /** All CHWs at the facility with performance metrics for the last 7–30 days. */
    @GetMapping("/chws")
    public ResponseEntity<List<SupervisorChwPerformanceResponse>> getChwPerformance() {
        return ResponseEntity.ok(dashboardService.getChwPerformance());
    }

    /** Full CHW profile: patient list with risk levels, last 10 home visits, 30-day stats. */
    @GetMapping("/chws/{chwId}")
    public ResponseEntity<SupervisorChwDetailResponse> getChwDetail(@PathVariable UUID chwId) {
        return ResponseEntity.ok(dashboardService.getChwDetail(chwId));
    }

    /** Active patients at HIGH or CRITICAL risk — for escalation decisions, highest risk first. */
    @GetMapping("/patients/high-risk")
    public ResponseEntity<List<FacilityPatientSummaryResponse>> getHighRiskPatients() {
        return ResponseEntity.ok(dashboardService.getHighRiskPatients());
    }

    /** Unresolved alerts assigned to CHWs at this facility. */
    @GetMapping("/alerts")
    public ResponseEntity<List<AlertResponse>> getFacilityAlerts() {
        return ResponseEntity.ok(dashboardService.getFacilityAlerts());
    }
}

package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.ChwDashboardResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.ChwPriorityListResponse;
import com.nelly.hivtbmonitoringsystem.service.ChwDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chw")
@RequiredArgsConstructor
public class ChwDashboardController {

    private final ChwDashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CHW')")
    public ResponseEntity<ChwDashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/priority-list")
    @PreAuthorize("hasRole('CHW')")
    public ResponseEntity<ChwPriorityListResponse> getPriorityList() {
        return ResponseEntity.ok(dashboardService.getPriorityList());
    }
}

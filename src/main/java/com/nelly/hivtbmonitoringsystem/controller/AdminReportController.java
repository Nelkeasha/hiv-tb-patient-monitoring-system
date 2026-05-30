package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class AdminReportController {

    private final AdminReportService reportService;

    /** System-wide report: users, facilities, patients, risk, adherence, alerts, stock, FHIR sync. */
    @GetMapping("/summary")
    public ResponseEntity<AdminReportResponse> getSummary() {
        return ResponseEntity.ok(reportService.generateSummary());
    }
}

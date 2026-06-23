package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.service.FacilityDashboardService;
import com.nelly.hivtbmonitoringsystem.service.FacilityReportService;
import com.nelly.hivtbmonitoringsystem.service.export.ClinicalCsvReportService;
import com.nelly.hivtbmonitoringsystem.service.export.ClinicalExcelReportService;
import com.nelly.hivtbmonitoringsystem.service.export.ClinicalPdfReportService;
import com.nelly.hivtbmonitoringsystem.service.export.support.ReportFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinical/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
public class FacilityDashboardController {

    private final FacilityDashboardService dashboardService;
    private final FacilityReportService reportService;
    private final ClinicalPdfReportService pdfReportService;
    private final ClinicalExcelReportService excelReportService;
    private final ClinicalCsvReportService csvReportService;

    /** Facility-level summary: patient counts, CHW count, risk distribution, adherence average. */
    @GetMapping("/stats")
    public ResponseEntity<FacilityStatsResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    /** All active patients at the facility with their latest risk level, highest risk first. */
    @GetMapping("/patients")
    public ResponseEntity<List<FacilityPatientSummaryResponse>> getPatients() {
        return ResponseEntity.ok(dashboardService.getPatients());
    }

    /** Full patient profile: demographics, latest AI risk score, unresolved alerts, recent home visits. */
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<FacilityPatientDetailResponse> getPatientDetail(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(dashboardService.getPatientDetail(patientId));
    }

    /** CHW list for this facility with per-CHW patient counts. */
    @GetMapping("/chws")
    public ResponseEntity<List<FacilityChwSummaryResponse>> getChws() {
        return ResponseEntity.ok(dashboardService.getChws());
    }

    /** Patients with medication adherence below threshold — for clinical intervention. */
    @GetMapping("/adherence/below-threshold")
    public ResponseEntity<List<FacilityPatientSummaryResponse>> getBelowThresholdPatients() {
        return ResponseEntity.ok(dashboardService.getBelowThresholdPatients());
    }

    /** Aggregated facility report: patient overview, risk distribution, adherence, referrals, alerts, CHW performance. */
    @GetMapping("/reports/summary")
    public ResponseEntity<FacilityReportResponse> getReportSummary() {
        return ResponseEntity.ok(reportService.generateSummary());
    }

    /** 7-day daily adherence trend — powers the clinical dashboard line chart. */
    @GetMapping("/adherence/trend")
    public ResponseEntity<List<DailyTrendPoint>> getAdherenceTrend() {
        return ResponseEntity.ok(dashboardService.getAdherenceTrend());
    }

    /** Facility report exported as PDF, Excel, or CSV — pick via ?format=pdf|excel|csv. */
    @GetMapping("/reports/summary/export")
    public ResponseEntity<byte[]> downloadReport(@RequestParam String format) {
        ReportFormat reportFormat = ReportFormat.fromParam(format);
        FacilityReportResponse report = reportService.generateSummary();

        byte[] body = switch (reportFormat) {
            case PDF -> pdfReportService.generate(report);
            case EXCEL -> excelReportService.generate(report);
            case CSV -> csvReportService.generate(report);
        };

        String filename = "facility-report-"
                + report.getGeneratedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + "." + reportFormat.fileExtension;
        return ResponseEntity.ok()
                .contentType(reportFormat.contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(body);
    }
}

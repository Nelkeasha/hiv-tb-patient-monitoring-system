package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.service.SupervisorDashboardService;
import com.nelly.hivtbmonitoringsystem.service.SupervisorReportService;
import com.nelly.hivtbmonitoringsystem.service.export.SupervisorCsvReportService;
import com.nelly.hivtbmonitoringsystem.service.export.SupervisorExcelReportService;
import com.nelly.hivtbmonitoringsystem.service.export.SupervisorPdfReportService;
import com.nelly.hivtbmonitoringsystem.service.export.support.ReportFormat;
import com.nelly.hivtbmonitoringsystem.service.report.SupervisorReportModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN', 'SYSTEM_ADMIN')")
public class SupervisorDashboardController {

    private final SupervisorDashboardService dashboardService;
    private final SupervisorReportService reportService;
    private final SupervisorReportModelService reportModelService;
    private final SupervisorPdfReportService pdfReportService;
    private final SupervisorExcelReportService excelReportService;
    private final SupervisorCsvReportService csvReportService;

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

    /** Aggregated supervisor report: workforce, patients, risk distribution, adherence, alerts, CHW performance. */
    @GetMapping("/reports/summary")
    public ResponseEntity<SupervisorReportResponse> getReportSummary() {
        return ResponseEntity.ok(reportService.generateSummary());
    }

    /** 7-day daily home-visits + missed-doses trend — powers the supervisor dashboard area chart. */
    @GetMapping("/activity/weekly")
    public ResponseEntity<List<DailyTrendPoint>> getWeeklyActivity() {
        return ResponseEntity.ok(dashboardService.getWeeklyActivity());
    }

    /**
     * Supervisor report exported as PDF, Excel, or CSV — pick via ?format=pdf|excel|csv.
     * Optional ?from=YYYY-MM-DD&to=YYYY-MM-DD sets the reporting period (default: last 30 days).
     * The PDF is the redesigned management report (executive summary, KPIs with
     * period-over-period deltas, recommendations, case line-listings); Excel/CSV
     * remain flat data exports for analysis/ingestion.
     */
    @GetMapping("/reports/summary/export")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        ReportFormat reportFormat = ReportFormat.fromParam(format);
        LocalDate fromDate = (from != null && !from.isBlank()) ? LocalDate.parse(from) : null;
        LocalDate toDate = (to != null && !to.isBlank()) ? LocalDate.parse(to) : null;

        byte[] body = switch (reportFormat) {
            case PDF -> pdfReportService.generate(reportModelService.build(fromDate, toDate));
            case EXCEL -> excelReportService.generate(reportModelService.build(fromDate, toDate));
            case CSV -> csvReportService.generate(reportService.generateSummary());
        };

        String filename = "supervisor-report-" + LocalDate.now() + "." + reportFormat.fileExtension;
        return ResponseEntity.ok()
                .contentType(reportFormat.contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(body);
    }
}

package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.service.AdminReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminCsvReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminExcelReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminPdfReportService;
import com.nelly.hivtbmonitoringsystem.service.export.support.ReportFormat;
import com.nelly.hivtbmonitoringsystem.service.report.AdminReportModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
public class AdminReportController {

    private final AdminReportService reportService;
    private final AdminReportModelService reportModelService;
    private final AdminPdfReportService pdfReportService;
    private final AdminExcelReportService excelReportService;
    private final AdminCsvReportService csvReportService;

    /** System-wide report: users, facilities, patients, risk, adherence, alerts, stock, FHIR sync. */
    @GetMapping("/summary")
    public ResponseEntity<AdminReportResponse> getSummary() {
        return ResponseEntity.ok(reportService.generateSummary());
    }

    /**
     * System-wide report exported as PDF, Excel, or CSV — pick via ?format=pdf|excel|csv.
     * Optional ?from=YYYY-MM-DD&to=YYYY-MM-DD sets the reporting period (default: last 30 days).
     * PDF/Excel are the redesigned management report; CSV stays a flat data export.
     */
    @GetMapping("/summary/export")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        ReportFormat reportFormat = ReportFormat.fromParam(format);
        java.time.LocalDate fromDate = (from != null && !from.isBlank()) ? java.time.LocalDate.parse(from) : null;
        java.time.LocalDate toDate = (to != null && !to.isBlank()) ? java.time.LocalDate.parse(to) : null;

        byte[] body = switch (reportFormat) {
            case PDF -> pdfReportService.generate(reportModelService.build(fromDate, toDate));
            case EXCEL -> excelReportService.generate(reportModelService.build(fromDate, toDate));
            case CSV -> csvReportService.generate(reportService.generateSummary());
        };

        String filename = "admin-report-"
                + java.time.LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + "." + reportFormat.fileExtension;
        return ResponseEntity.ok()
                .contentType(reportFormat.contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(body);
    }
}

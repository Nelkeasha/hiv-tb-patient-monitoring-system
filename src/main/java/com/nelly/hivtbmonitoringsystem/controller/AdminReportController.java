package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.service.AdminReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminCsvReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminExcelReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminPdfReportService;
import com.nelly.hivtbmonitoringsystem.service.export.support.ReportFormat;
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
    private final AdminPdfReportService pdfReportService;
    private final AdminExcelReportService excelReportService;
    private final AdminCsvReportService csvReportService;

    /** System-wide report: users, facilities, patients, risk, adherence, alerts, stock, FHIR sync. */
    @GetMapping("/summary")
    public ResponseEntity<AdminReportResponse> getSummary() {
        return ResponseEntity.ok(reportService.generateSummary());
    }

    /** System-wide report exported as PDF, Excel, or CSV — pick via ?format=pdf|excel|csv. */
    @GetMapping("/summary/export")
    public ResponseEntity<byte[]> downloadReport(@RequestParam String format) {
        ReportFormat reportFormat = ReportFormat.fromParam(format);
        AdminReportResponse report = reportService.generateSummary();

        byte[] body = switch (reportFormat) {
            case PDF -> pdfReportService.generate(report);
            case EXCEL -> excelReportService.generate(report);
            case CSV -> csvReportService.generate(report);
        };

        String filename = "admin-report-"
                + report.getGeneratedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + "." + reportFormat.fileExtension;
        return ResponseEntity.ok()
                .contentType(reportFormat.contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(body);
    }
}

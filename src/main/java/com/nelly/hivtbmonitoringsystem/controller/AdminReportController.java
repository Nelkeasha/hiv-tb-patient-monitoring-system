package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.service.AdminReportService;
import com.nelly.hivtbmonitoringsystem.service.export.AdminExcelReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
public class AdminReportController {

    private final AdminReportService reportService;
    private final AdminExcelReportService excelReportService;

    /** System-wide report: users, facilities, patients, risk, adherence, alerts, stock, FHIR sync. */
    @GetMapping("/summary")
    public ResponseEntity<AdminReportResponse> getSummary() {
        return ResponseEntity.ok(reportService.generateSummary());
    }

    /** System-wide report as a multi-sheet Excel workbook, for admin analysis. */
    @GetMapping("/summary/excel")
    public ResponseEntity<byte[]> downloadSummaryExcel() {
        AdminReportResponse report = reportService.generateSummary();
        byte[] excel = excelReportService.generate(report);
        String filename = "admin-report-" + report.getGeneratedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(excel);
    }
}

package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.ChwPerformanceRow;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportResponse;
import com.nelly.hivtbmonitoringsystem.service.export.support.ExcelReportBuilder;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Renders the facility (clinical) report as a multi-sheet Excel workbook —
 * the analysis-friendly counterpart to the official PDF, for providers who
 * want to pivot/sort the CHW performance table themselves.
 */
@Service
public class ClinicalExcelReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final ExcelReportBuilder builder = new ExcelReportBuilder();

    public byte[] generate(FacilityReportResponse report) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle sectionStyle = builder.sectionStyle(wb);
            CellStyle headerStyle = builder.headerStyle(wb);
            CellStyle labelStyle = builder.labelStyle(wb);

            buildSummarySheet(wb, report, sectionStyle, labelStyle);
            buildChwSheet(wb, report, headerStyle);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private void buildSummarySheet(XSSFWorkbook wb, FacilityReportResponse r,
                                    CellStyle sectionStyle, CellStyle labelStyle) {
        Sheet sheet = wb.createSheet("Summary");
        int rowIdx = 0;

        Row title = sheet.createRow(rowIdx++);
        title.createCell(0).setCellValue("HIV/TB Monitoring System — Facility Clinical Report");
        title.getCell(0).setCellStyle(sectionStyle);
        Row genRow = sheet.createRow(rowIdx++);
        genRow.createCell(0).setCellValue("Facility");
        genRow.createCell(1).setCellValue(r.getFacilityName() != null ? r.getFacilityName() : "-");
        Row districtRow = sheet.createRow(rowIdx++);
        districtRow.createCell(0).setCellValue("District");
        districtRow.createCell(1).setCellValue(r.getDistrict() != null ? r.getDistrict() : "-");
        Row genTsRow = sheet.createRow(rowIdx++);
        genTsRow.createCell(0).setCellValue("Generated");
        genTsRow.createCell(1).setCellValue(r.getGeneratedAt() != null ? r.getGeneratedAt().format(TS) : "-");
        rowIdx++;

        rowIdx = builder.section(sheet, rowIdx, "Patient Overview", sectionStyle, labelStyle, new Object[][]{
                {"Total Active Patients", r.getTotalActivePatients()},
                {"HIV Only", r.getHivOnly()},
                {"TB Only", r.getTbOnly()},
                {"HIV + TB Co-infection", r.getHivTbCoinfection()},
        });

        rowIdx = builder.section(sheet, rowIdx, "Risk Distribution", sectionStyle, labelStyle, new Object[][]{
                {"Low", r.getRiskLow()},
                {"Moderate", r.getRiskModerate()},
                {"High", r.getRiskHigh()},
                {"Critical", r.getRiskCritical()},
                {"Unscored", r.getRiskUnscored()},
        });

        rowIdx = builder.section(sheet, rowIdx, "Adherence", sectionStyle, labelStyle, new Object[][]{
                {"Facility Adherence Average (%)", r.getFacilityAdherenceAvg()},
                {"Below Threshold (Patients)", r.getBelowThresholdCount()},
                {"False Confirmation Flags", r.getFalseConfirmationFlagCount()},
        });

        rowIdx = builder.section(sheet, rowIdx, "Referrals", sectionStyle, labelStyle, new Object[][]{
                {"Total", r.getReferralTotal()},
                {"Pending", r.getReferralPending()},
                {"Confirmed", r.getReferralConfirmed()},
                {"Attended", r.getReferralAttended()},
                {"Not Attended", r.getReferralNotAttended()},
                {"Cancelled", r.getReferralCancelled()},
        });

        builder.section(sheet, rowIdx, "Alerts (Unresolved)", sectionStyle, labelStyle, new Object[][]{
                {"Total Unresolved", r.getUnresolvedAlerts()},
                {"Critical", r.getCriticalAlerts()},
                {"Warning", r.getWarningAlerts()},
        });

        sheet.setColumnWidth(0, 32 * 256);
        sheet.setColumnWidth(1, 16 * 256);
    }

    private void buildChwSheet(XSSFWorkbook wb, FacilityReportResponse r, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("CHW Performance");
        String[] headers = {"CHW Name", "Employee Code", "Village", "Active Patients", "Visits (30d)", "Missed Doses (30d)"};
        builder.tableHeader(sheet, headers, headerStyle);

        int rowIdx = 1;
        if (r.getChwPerformance() != null) {
            for (ChwPerformanceRow row : r.getChwPerformance()) {
                Row dataRow = sheet.createRow(rowIdx++);
                dataRow.createCell(0).setCellValue(row.getChwName() != null ? row.getChwName() : "-");
                dataRow.createCell(1).setCellValue(row.getEmployeeCode() != null ? row.getEmployeeCode() : "-");
                dataRow.createCell(2).setCellValue(row.getAssignedVillage() != null ? row.getAssignedVillage() : "-");
                dataRow.createCell(3).setCellValue(row.getActivePatients());
                dataRow.createCell(4).setCellValue(row.getVisitsLast30Days());
                dataRow.createCell(5).setCellValue(row.getMissedDosesLast30Days());
            }
        }
    }
}

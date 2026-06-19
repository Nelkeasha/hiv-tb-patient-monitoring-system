package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Renders the system-wide admin report as a multi-sheet Excel workbook —
 * built for analysis (pivoting, filtering, sorting), not for printing.
 */
@Service
public class AdminExcelReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public byte[] generate(AdminReportResponse report) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle sectionStyle = sectionStyle(wb);
            CellStyle headerStyle = headerStyle(wb);
            CellStyle labelStyle = labelStyle(wb);

            buildSummarySheet(wb, report, sectionStyle, labelStyle);
            buildFacilitySheet(wb, report, headerStyle);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private void buildSummarySheet(XSSFWorkbook wb, AdminReportResponse r,
                                    CellStyle sectionStyle, CellStyle labelStyle) {
        Sheet sheet = wb.createSheet("Summary");
        int rowIdx = 0;

        Row title = sheet.createRow(rowIdx++);
        title.createCell(0).setCellValue("HIV/TB Monitoring System — System-Wide Admin Report");
        title.getCell(0).setCellStyle(sectionStyle);
        Row genRow = sheet.createRow(rowIdx++);
        genRow.createCell(0).setCellValue("Generated");
        genRow.createCell(1).setCellValue(r.getGeneratedAt() != null ? r.getGeneratedAt().format(TS) : "-");
        rowIdx++;

        rowIdx = section(sheet, rowIdx, "Users & Workforce", sectionStyle, labelStyle, new Object[][]{
                {"Total Users", r.getTotalUsers()},
                {"Active Users", r.getActiveUsers()},
                {"Inactive Users", r.getInactiveUsers()},
                {"Community Health Workers", r.getTotalChw()},
                {"Facility Providers", r.getTotalProviders()},
                {"Supervisors", r.getTotalSupervisors()},
                {"Patient Accounts", r.getTotalPatients()},
        });

        rowIdx = section(sheet, rowIdx, "Facilities", sectionStyle, labelStyle, new Object[][]{
                {"Total Facilities", r.getTotalFacilities()},
                {"Active Facilities", r.getActiveFacilities()},
        });

        rowIdx = section(sheet, rowIdx, "Patients (System-Wide)", sectionStyle, labelStyle, new Object[][]{
                {"Total Active Patients", r.getTotalActivePatients()},
                {"HIV Only", r.getHivOnly()},
                {"TB Only", r.getTbOnly()},
                {"HIV + TB Co-infection", r.getHivTbCoinfection()},
        });

        rowIdx = section(sheet, rowIdx, "Risk Distribution", sectionStyle, labelStyle, new Object[][]{
                {"Low", r.getRiskLow()},
                {"Moderate", r.getRiskModerate()},
                {"High", r.getRiskHigh()},
                {"Critical", r.getRiskCritical()},
                {"Unscored", r.getRiskUnscored()},
        });

        rowIdx = section(sheet, rowIdx, "Adherence", sectionStyle, labelStyle, new Object[][]{
                {"System Adherence Average (%)", r.getSystemAdherenceAvg()},
                {"Below Threshold (Patients)", r.getBelowThresholdCount()},
                {"False Confirmation Flags", r.getFalseConfirmationFlagCount()},
        });

        rowIdx = section(sheet, rowIdx, "Alerts (Unresolved)", sectionStyle, labelStyle, new Object[][]{
                {"Total Unresolved", r.getUnresolvedAlerts()},
                {"Critical", r.getCriticalAlerts()},
                {"Warning", r.getWarningAlerts()},
                {"Missed Dose", r.getMissedDoseAlerts()},
        });

        rowIdx = section(sheet, rowIdx, "FHIR Sync Status", sectionStyle, labelStyle, new Object[][]{
                {"Pending", r.getFhirSyncPending()},
                {"Synced", r.getFhirSyncSynced()},
                {"Failed", r.getFhirSyncFailed()},
        });

        section(sheet, rowIdx, "LTFU Tracing", sectionStyle, labelStyle, new Object[][]{
                {"Active Tasks", r.getActiveLtfuTasks()},
                {"Confirmed LTFU", r.getLtfuConfirmedCount()},
                {"Escalated", r.getEscalatedCount()},
        });

        sheet.setColumnWidth(0, 32 * 256);
        sheet.setColumnWidth(1, 16 * 256);
    }

    private int section(Sheet sheet, int rowIdx, String title, CellStyle sectionStyle,
                         CellStyle labelStyle, Object[][] rows) {
        Row header = sheet.createRow(rowIdx++);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(title);
        headerCell.setCellStyle(sectionStyle);

        for (Object[] kv : rows) {
            Row row = sheet.createRow(rowIdx++);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue((String) kv[0]);
            labelCell.setCellStyle(labelStyle);

            Object value = kv[1];
            Cell valueCell = row.createCell(1);
            if (value instanceof Number) {
                valueCell.setCellValue(((Number) value).doubleValue());
            } else {
                valueCell.setCellValue(value != null ? value.toString() : "-");
            }
        }
        return rowIdx + 1; // blank spacer row between sections
    }

    private void buildFacilitySheet(XSSFWorkbook wb, AdminReportResponse r, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("Facility Breakdown");
        String[] headers = {"Facility", "District", "Active Patients", "Total CHWs",
                "Adherence Avg (%)", "High Risk Patients", "Unresolved Alerts"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        if (r.getFacilityBreakdown() != null) {
            for (FacilityReportRow row : r.getFacilityBreakdown()) {
                Row dataRow = sheet.createRow(rowIdx++);
                dataRow.createCell(0).setCellValue(row.getFacilityName() != null ? row.getFacilityName() : "-");
                dataRow.createCell(1).setCellValue(row.getDistrict() != null ? row.getDistrict() : "-");
                dataRow.createCell(2).setCellValue(row.getActivePatients());
                dataRow.createCell(3).setCellValue(row.getTotalChws());
                dataRow.createCell(4).setCellValue(row.getAdherenceAvg() != null ? row.getAdherenceAvg().doubleValue() : 0);
                dataRow.createCell(5).setCellValue(row.getHighRiskPatients());
                dataRow.createCell(6).setCellValue(row.getUnresolvedAlerts());
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }
    }

    private CellStyle sectionStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                new byte[]{0x00, 0x6D, 0x77}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle headerStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                new byte[]{0x00, 0x6D, 0x77}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle labelStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setItalic(false);
        style.setFont(font);
        return style;
    }
}

package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorChwReportRow;
import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorReportResponse;
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
 * Renders the supervisor's programme report as a multi-sheet Excel
 * workbook — the analysis-friendly counterpart to the CSV export, for
 * supervisors who want to pivot/sort the CHW performance table themselves.
 */
@Service
public class SupervisorExcelReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final ExcelReportBuilder builder = new ExcelReportBuilder();

    public byte[] generate(SupervisorReportResponse report) {
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

    private void buildSummarySheet(XSSFWorkbook wb, SupervisorReportResponse r,
                                    CellStyle sectionStyle, CellStyle labelStyle) {
        Sheet sheet = wb.createSheet("Summary");
        int rowIdx = 0;

        Row title = sheet.createRow(rowIdx++);
        title.createCell(0).setCellValue("HIV/TB Monitoring System — Supervisor Programme Report");
        title.getCell(0).setCellStyle(sectionStyle);
        Row genRow = sheet.createRow(rowIdx++);
        genRow.createCell(0).setCellValue("Generated");
        genRow.createCell(1).setCellValue(r.getGeneratedAt() != null ? r.getGeneratedAt().format(TS) : "-");
        rowIdx++;

        rowIdx = builder.section(sheet, rowIdx, "Workforce", sectionStyle, labelStyle, new Object[][]{
                {"Total CHWs", r.getTotalChws()},
                {"Active CHWs", r.getActiveChws()},
        });

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

        rowIdx = builder.section(sheet, rowIdx, "Adherence & Activity", sectionStyle, labelStyle, new Object[][]{
                {"Facility Adherence Average (%)", r.getFacilityAdherenceAvg()},
                {"Below Threshold (Patients)", r.getBelowThresholdCount()},
                {"Home Visits (30d)", r.getTotalHomeVisits30d()},
                {"Missed Doses (7d)", r.getTotalMissedDoses7d()},
        });

        builder.section(sheet, rowIdx, "Alerts (Unresolved)", sectionStyle, labelStyle, new Object[][]{
                {"Total Unresolved", r.getUnresolvedAlerts()},
                {"Critical", r.getCriticalAlerts()},
                {"Warning", r.getWarningAlerts()},
                {"Missed Dose", r.getMissedDoseAlerts()},
                {"Early Warning", r.getEarlyWarningAlerts()},
        });

        sheet.setColumnWidth(0, 32 * 256);
        sheet.setColumnWidth(1, 16 * 256);
    }

    private void buildChwSheet(XSSFWorkbook wb, SupervisorReportResponse r, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("CHW Performance");
        String[] headers = {"CHW Name", "Employee Code", "Village", "Active Patients", "High Risk Patients", "Home Visits (30d)", "Missed Doses (7d)"};
        builder.tableHeader(sheet, headers, headerStyle);

        int rowIdx = 1;
        if (r.getChwPerformance() != null) {
            for (SupervisorChwReportRow row : r.getChwPerformance()) {
                Row dataRow = sheet.createRow(rowIdx++);
                dataRow.createCell(0).setCellValue(row.getChwName() != null ? row.getChwName() : "-");
                dataRow.createCell(1).setCellValue(row.getEmployeeCode() != null ? row.getEmployeeCode() : "-");
                dataRow.createCell(2).setCellValue(row.getAssignedVillage() != null ? row.getAssignedVillage() : "-");
                dataRow.createCell(3).setCellValue(row.getActivePatients());
                dataRow.createCell(4).setCellValue(row.getHighRiskPatients());
                dataRow.createCell(5).setCellValue(row.getHomeVisits30d());
                dataRow.createCell(6).setCellValue(row.getMissedDoses7d());
            }
        }
    }
}

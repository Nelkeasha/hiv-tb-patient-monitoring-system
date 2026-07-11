package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.report.Indicator;
import com.nelly.hivtbmonitoringsystem.dto.report.KvSection;
import com.nelly.hivtbmonitoringsystem.dto.report.LineListing;
import com.nelly.hivtbmonitoringsystem.dto.report.Recommendation;
import com.nelly.hivtbmonitoringsystem.dto.report.ReportModel;
import com.nelly.hivtbmonitoringsystem.service.export.support.ExcelReportBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Renders a {@link ReportModel} as a multi-sheet Excel workbook — the
 * analysis-friendly counterpart to the PDF management report. Carries the same
 * content (executive summary, indicators with period-over-period comparison,
 * recommendations, and case line-listings) but laid out as sortable/pivotable
 * sheets rather than a formatted document.
 */
@Service
public class SupervisorExcelReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final ExcelReportBuilder builder = new ExcelReportBuilder();

    public byte[] generate(ReportModel model) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle sectionStyle = builder.sectionStyle(wb);
            CellStyle headerStyle = builder.headerStyle(wb);
            CellStyle labelStyle = builder.labelStyle(wb);
            CellStyle boldStyle = boldStyle(wb);

            buildSummarySheet(wb, model, sectionStyle, headerStyle, labelStyle, boldStyle);
            buildRecommendationsSheet(wb, model, headerStyle);
            buildBreakdownsSheet(wb, model, sectionStyle, labelStyle);
            buildLineListingSheets(wb, model, headerStyle);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    // ── Summary: meta + executive summary + indicator comparison ────────────────
    private void buildSummarySheet(XSSFWorkbook wb, ReportModel m, CellStyle sectionStyle,
                                   CellStyle headerStyle, CellStyle labelStyle, CellStyle boldStyle) {
        Sheet sheet = wb.createSheet("Summary");
        int r = 0;

        r = titleRow(sheet, r, "HIV/TB Monitoring System — " + safe(m.getReportTitle()), sectionStyle);
        r = kv(sheet, r, "Facility", safe(m.getScopeName()), boldStyle);
        if (m.getSubScope() != null) r = kv(sheet, r, "District", m.getSubScope(), boldStyle);
        r = kv(sheet, r, "Period", safe(m.getPeriodLabel()), boldStyle);
        r = kv(sheet, r, "Comparison", safe(m.getComparisonLabel()), boldStyle);
        r = kv(sheet, r, "Prepared by", safe(m.getGeneratedBy()), boldStyle);
        r = kv(sheet, r, "Generated", m.getGeneratedAt() != null ? m.getGeneratedAt().format(TS) : "-", boldStyle);
        r++;

        // Executive summary
        Row esHeader = sheet.createRow(r++);
        Cell esCell = esHeader.createCell(0);
        esCell.setCellValue("Executive Summary");
        esCell.setCellStyle(sectionStyle);
        if (m.getExecutiveSummary() != null) {
            for (String point : m.getExecutiveSummary()) {
                if (point == null || point.isBlank()) continue;
                int sep = point.indexOf("||");
                String lead = sep >= 0 ? point.substring(0, sep).trim() : "";
                String body = sep >= 0 ? point.substring(sep + 2).trim() : point.trim();
                Row row = sheet.createRow(r++);
                Cell c0 = row.createCell(0);
                c0.setCellValue(lead);
                c0.setCellStyle(boldStyle);
                row.createCell(1).setCellValue(body);
            }
        }
        r++;

        // Indicators with comparison
        Row indHeader = sheet.createRow(r++);
        Cell indCell = indHeader.createCell(0);
        indCell.setCellValue("Programme Indicators");
        indCell.setCellStyle(sectionStyle);
        String[] cols = {"Indicator", "Value", "Previous", "Change", "Status", "Target"};
        Row colRow = sheet.createRow(r++);
        for (int i = 0; i < cols.length; i++) {
            Cell c = colRow.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(headerStyle);
        }
        if (m.getIndicators() != null) {
            for (Indicator ind : m.getIndicators()) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(safe(ind.getLabel()));
                row.createCell(1).setCellValue(safe(ind.getValue()));
                row.createCell(2).setCellValue(ind.getPreviousValue() != null ? ind.getPreviousValue() : "-");
                row.createCell(3).setCellValue(ind.getDeltaLabel() != null ? ind.getDeltaLabel() : "-");
                row.createCell(4).setCellValue(ind.getStatus() != null ? ind.getStatus().name() : "-");
                row.createCell(5).setCellValue(ind.getTarget() != null ? ind.getTarget() : "-");
            }
        }

        sheet.setColumnWidth(0, 34 * 256);
        for (int i = 1; i <= 5; i++) sheet.setColumnWidth(i, 18 * 256);
    }

    // ── Recommendations ─────────────────────────────────────────────────────────
    private void buildRecommendationsSheet(XSSFWorkbook wb, ReportModel m, CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("Recommendations");
        String[] headers = {"#", "Priority", "Finding", "Suggested Action", "Owner"};
        builder.tableHeader(sheet, headers, headerStyle);
        int r = 1;
        if (m.getRecommendations() != null) {
            int i = 1;
            for (Recommendation rec : m.getRecommendations()) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(i++);
                row.createCell(1).setCellValue(rec.getSeverity() != null ? rec.getSeverity().name() : "-");
                row.createCell(2).setCellValue(safe(rec.getFinding()));
                row.createCell(3).setCellValue(safe(rec.getAction()));
                row.createCell(4).setCellValue(safe(rec.getOwner()));
            }
        }
        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 45 * 256);
        sheet.setColumnWidth(3, 50 * 256);
        sheet.setColumnWidth(4, 18 * 256);
    }

    // ── Supporting breakdowns ───────────────────────────────────────────────────
    private void buildBreakdownsSheet(XSSFWorkbook wb, ReportModel m, CellStyle sectionStyle, CellStyle labelStyle) {
        Sheet sheet = wb.createSheet("Breakdowns");
        int r = 0;
        if (m.getKvSections() != null) {
            for (KvSection s : m.getKvSections()) {
                Object[][] rows = new Object[s.getRows().length][2];
                for (int i = 0; i < s.getRows().length; i++) {
                    rows[i][0] = s.getRows()[i][0];
                    rows[i][1] = s.getRows()[i].length > 1 ? s.getRows()[i][1] : "-";
                }
                r = builder.section(sheet, r, s.getTitle(), sectionStyle, labelStyle, rows);
            }
        }
        sheet.setColumnWidth(0, 32 * 256);
        sheet.setColumnWidth(1, 16 * 256);
    }

    // ── One sheet per case line-listing ─────────────────────────────────────────
    private void buildLineListingSheets(XSSFWorkbook wb, ReportModel m, CellStyle headerStyle) {
        if (m.getLineListings() == null) return;
        Set<String> used = new HashSet<>();
        for (LineListing l : m.getLineListings()) {
            Sheet sheet = wb.createSheet(uniqueSheetName(l.getTitle(), used));
            builder.tableHeader(sheet, l.getHeaders(), headerStyle);
            int r = 1;
            List<String[]> rows = l.getRows();
            if (rows == null || rows.isEmpty()) {
                sheet.createRow(r).createCell(0).setCellValue(safe(l.getEmptyMessage()));
            } else {
                for (String[] data : rows) {
                    Row row = sheet.createRow(r++);
                    for (int i = 0; i < data.length; i++) {
                        row.createCell(i).setCellValue(data[i] != null ? data[i] : "-");
                    }
                }
            }
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────
    private int titleRow(Sheet sheet, int r, String title, CellStyle style) {
        Row row = sheet.createRow(r++);
        Cell c = row.createCell(0);
        c.setCellValue(title);
        c.setCellStyle(style);
        return r;
    }

    private int kv(Sheet sheet, int r, String label, String value, CellStyle boldStyle) {
        Row row = sheet.createRow(r++);
        Cell c0 = row.createCell(0);
        c0.setCellValue(label);
        c0.setCellStyle(boldStyle);
        row.createCell(1).setCellValue(value);
        return r;
    }

    private CellStyle boldStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /** Excel sheet names: max 31 chars, no []:*?/\ and must be unique. */
    private String uniqueSheetName(String title, Set<String> used) {
        String base = title == null ? "List" : title;
        int cut = base.indexOf(" (");
        if (cut > 0) base = base.substring(0, cut);
        cut = base.indexOf(" - ");
        if (cut > 0) base = base.substring(cut + 3); // keep the descriptive tail
        base = base.replaceAll("[\\[\\]:*?/\\\\]", " ").trim();
        if (base.length() > 28) base = base.substring(0, 28).trim();
        if (base.isEmpty()) base = "List";
        String name = base;
        int n = 2;
        while (used.contains(name.toLowerCase())) {
            String suffix = " " + n++;
            name = base.substring(0, Math.min(base.length(), 28 - suffix.length())) + suffix;
        }
        used.add(name.toLowerCase());
        return name;
    }

    private static String safe(String s) { return s != null ? s : "-"; }
}

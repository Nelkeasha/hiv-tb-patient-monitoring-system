package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.report.ReportModel;
import com.nelly.hivtbmonitoringsystem.service.export.support.ExcelReportBuilder;
import com.nelly.hivtbmonitoringsystem.service.export.support.ReportExcelWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Renders a facility (clinical) {@link ReportModel} as a multi-sheet Excel
 * workbook — the analysis-friendly counterpart to the PDF management report.
 */
@Service
public class ClinicalExcelReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private final ExcelReportBuilder builder = new ExcelReportBuilder();

    public byte[] generate(ReportModel model) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle sectionStyle = builder.sectionStyle(wb);
            CellStyle headerStyle = builder.headerStyle(wb);
            CellStyle labelStyle = builder.labelStyle(wb);
            CellStyle boldStyle = boldStyle(wb);

            ReportExcelWriter.buildSummarySheet(wb, model, builder, sectionStyle, headerStyle, boldStyle, TS);
            ReportExcelWriter.buildRecommendationsSheet(wb, model, builder, headerStyle);
            ReportExcelWriter.buildBreakdownsSheet(wb, model, builder, sectionStyle, labelStyle);
            ReportExcelWriter.buildLineListingSheets(wb, model, builder, headerStyle);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private CellStyle boldStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}

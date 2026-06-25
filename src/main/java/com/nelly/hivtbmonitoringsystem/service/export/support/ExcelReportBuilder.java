package com.nelly.hivtbmonitoringsystem.service.export.support;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Shared styling/layout building blocks for every multi-sheet Excel report
 * (Admin, Clinical, Supervisor) — DMC red-branded section/header rows and the
 * sectioned key-value row writer, so each report's Excel service only needs
 * to supply its own data, not re-implement cell styling.
 */
public class ExcelReportBuilder {

    private static final byte[] BRAND_RGB = {(byte)0xD1, (byte)0x2C, (byte)0x1F};

    public CellStyle sectionStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(BRAND_RGB, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public CellStyle headerStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(BRAND_RGB, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    public CellStyle labelStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setItalic(false);
        style.setFont(font);
        return style;
    }

    /** Writes a section header row followed by one row per key/value pair, plus a blank spacer row. Returns the next free row index. */
    public int section(Sheet sheet, int rowIdx, String title, CellStyle sectionStyle,
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

    /** Writes a centered, teal header row for a flat data table sheet. */
    public void tableHeader(Sheet sheet, String[] headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }
    }
}

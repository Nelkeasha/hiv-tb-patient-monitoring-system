package com.nelly.hivtbmonitoringsystem.service.export.support;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Shared layout/styling building blocks for every "official PDF" report
 * (Clinical, Admin, Supervisor) so the DMC letterhead, brand header,
 * section titles, key-value tables, and data tables stay visually identical
 * across reports without each report service re-implementing the same
 * iText/OpenPDF boilerplate.
 */
public class PdfReportBuilder {

    /** DMC brand red — #D12C1F */
    public static final Color BRAND = new Color(0xD1, 0x2C, 0x1F);
    /** Light red tint for alternating row backgrounds */
    public static final Color LIGHT = new Color(0xFD, 0xDC, 0xDA);

    /** Letterhead organisation name displayed in the header band. */
    private static final String ORG_HEADER_NAME = "Dream Medical Center Hospital";
    /**
     * Contact line displayed in the footer band.
     * Update this string (or externalise to application.properties) to change
     * the org contact details shown on every generated report.
     */
    private static final String ORG_CONTACT_LINE =
            "Kigali, Rwanda  ·  Tel: +250 XXX XXX XXX  ·  info@dreammedical.rw  ·  www.dreammedical.rw";

    private final Document doc;
    private final ByteArrayOutputStream out;

    private final Font titleFont      = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BRAND);
    private final Font subtitleFont   = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
    private final Font metaFont       = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
    private final Font sectionFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BRAND);
    private final Font labelFont      = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
    private final Font valueFont      = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
    private final Font tableHeadFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private final Font tableCellFont  = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

    public PdfReportBuilder() {
        try {
            this.out = new ByteArrayOutputStream();
            this.doc = new Document(PageSize.A4, 42, 42, 56, 48);
            PdfWriter.getInstance(doc, out);
            doc.open();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PDF document", e);
        }
    }

    public PdfReportBuilder header(String subtitle, String metaLine) {
        try {
            // DMC letterhead header band
            PdfPTable headerBand = new PdfPTable(1);
            headerBand.setWidthPercentage(100);
            headerBand.setSpacingAfter(10);
            PdfPCell orgCell = new PdfPCell(new Phrase(ORG_HEADER_NAME,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
            orgCell.setBackgroundColor(BRAND);
            orgCell.setPadding(7);
            orgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerBand.addCell(orgCell);
            doc.add(headerBand);

            Paragraph title = new Paragraph("HIV/TB MONITORING SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph sub = new Paragraph(subtitle, subtitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(4);
            doc.add(sub);

            Paragraph meta = new Paragraph(metaLine, metaFont);
            meta.setAlignment(Element.ALIGN_CENTER);
            meta.setSpacingAfter(18);
            doc.add(meta);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF header", e);
        }
    }

    public PdfReportBuilder section(String title, String[][] kvRows) {
        try {
            doc.add(sectionTitle(title));
            doc.add(kvTable(kvRows));
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF section: " + title, e);
        }
    }

    public PdfReportBuilder dataTable(String title, String[] headers, List<String[]> rows, String emptyMessage) {
        try {
            doc.add(sectionTitle(title));
            doc.add(buildDataTable(headers, rows, emptyMessage));
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF table: " + title, e);
        }
    }

    public PdfReportBuilder footer(String text) {
        try {
            Paragraph footer = new Paragraph(text, metaFont);
            footer.setSpacingBefore(20);
            footer.setSpacingAfter(8);
            doc.add(footer);

            // DMC letterhead footer band
            PdfPTable footerBand = new PdfPTable(1);
            footerBand.setWidthPercentage(100);
            PdfPCell contactCell = new PdfPCell(new Phrase(ORG_CONTACT_LINE,
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.WHITE)));
            contactCell.setBackgroundColor(BRAND);
            contactCell.setPadding(6);
            contactCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerBand.addCell(contactCell);
            doc.add(footerBand);

            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF footer", e);
        }
    }

    public byte[] build() {
        doc.close();
        return out.toByteArray();
    }

    private Paragraph sectionTitle(String text) {
        Paragraph p = new Paragraph(text, sectionFont);
        p.setSpacingBefore(14);
        p.setSpacingAfter(6);
        return p;
    }

    private PdfPTable kvTable(String[][] rows) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2.2f, 1f});
        } catch (Exception ignored) {
            // widths are advisory; default split is fine if this fails
        }
        boolean shaded = false;
        for (String[] row : rows) {
            PdfPCell labelCell = new PdfPCell(new Phrase(row[0], labelFont));
            PdfPCell valueCell = new PdfPCell(new Phrase(row[1], valueFont));
            for (PdfPCell c : new PdfPCell[]{labelCell, valueCell}) {
                c.setPadding(6);
                c.setBorderColor(new Color(0xDC, 0xEC, 0xF0));
                c.setBackgroundColor(shaded ? LIGHT : Color.WHITE);
            }
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(labelCell);
            table.addCell(valueCell);
            shaded = !shaded;
        }
        return table;
    }

    private PdfPTable buildDataTable(String[] headers, List<String[]> rows, String emptyMessage) {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, tableHeadFont));
            cell.setBackgroundColor(BRAND);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        if (rows == null || rows.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase(emptyMessage, tableCellFont));
            empty.setColspan(headers.length);
            empty.setPadding(8);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
            return table;
        }

        boolean shaded = false;
        for (String[] row : rows) {
            for (String v : row) {
                PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "-", tableCellFont));
                cell.setPadding(5);
                cell.setBackgroundColor(shaded ? LIGHT : Color.WHITE);
                table.addCell(cell);
            }
            shaded = !shaded;
        }
        return table;
    }

    public static String formatPct(BigDecimal pct) {
        if (pct == null) return "0%";
        return pct.setScale(1, RoundingMode.HALF_UP) + "%";
    }

    public static String nullSafe(String s) {
        return s != null ? s : "-";
    }
}

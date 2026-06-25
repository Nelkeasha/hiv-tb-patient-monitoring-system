package com.nelly.hivtbmonitoringsystem.service.export.support;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PdfReportBuilder {

    // ── DMC brand palette ────────────────────────────────────────────────────
    /** Primary brand orange  #D9643A */
    public static final Color BRAND       = new Color(0xD9, 0x64, 0x3A);
    /** Darker orange for sub-elements  #C9552F */
    public static final Color BRAND_DARK  = new Color(0xC9, 0x55, 0x2F);
    /** Very light orange tint for alternating rows  #FEF0EB */
    public static final Color BRAND_LIGHT = new Color(0xFE, 0xF0, 0xEB);
    /** Near-black body text  #2C2C2C */
    public static final Color TEXT_DARK   = new Color(0x2C, 0x2C, 0x2C);
    /** Mid-grey secondary text  #6B7280 */
    public static final Color TEXT_GREY   = new Color(0x6B, 0x72, 0x80);
    /** Light border colour  #E9E9E9 */
    public static final Color BORDER      = new Color(0xE9, 0xE9, 0xE9);

    // ── Letterhead strings ───────────────────────────────────────────────────
    private static final String ORG_NAME    = "DREAM MEDICAL CENTER HOSPITAL";
    private static final String ORG_MOTTO   = "Where Science and Faith meet to bring healing";
    private static final String ORG_ADDRESS = "Kigali, Kicukiro, Rwanda";
    private static final String ORG_PHONE   = "+250 782 749 660";
    private static final String ORG_EMAIL   = "info@dreammedicalcenter.rw";
    private static final String ORG_WEB     = "www.dreammedicalcenter.rw";
    private static final String SYS_NAME    = "HIV/TB Patient Monitoring System";

    // ── Font definitions ─────────────────────────────────────────────────────
    private final Font orgNameFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.WHITE);
    private final Font mottoFont     = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(0xFF, 0xE8, 0xD9));
    private final Font sysNameFont   = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.WHITE);
    private final Font titleFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BRAND);
    private final Font metaFont      = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GREY);
    private final Font sectionFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BRAND);
    private final Font labelFont     = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_GREY);
    private final Font valueFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, TEXT_DARK);
    private final Font tableHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
    private final Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
    private final Font footerFont    = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.WHITE);

    private final Document doc;
    private final ByteArrayOutputStream out;

    public PdfReportBuilder() {
        try {
            this.out = new ByteArrayOutputStream();
            this.doc = new Document(PageSize.A4, 44, 44, 56, 50);
            PdfWriter.getInstance(doc, out);
            doc.open();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise PDF document", e);
        }
    }

    public PdfReportBuilder header(String subtitle, String metaLine) {
        try {
            // ── Top letterhead band ──────────────────────────────────────────
            PdfPTable top = new PdfPTable(1);
            top.setWidthPercentage(100);
            top.setSpacingAfter(0);

            PdfPCell nameCell = new PdfPCell();
            nameCell.setBackgroundColor(BRAND);
            nameCell.setPadding(10);
            nameCell.setPaddingBottom(4);
            nameCell.setBorder(Rectangle.NO_BORDER);
            nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph namePara = new Paragraph(ORG_NAME, orgNameFont);
            namePara.setAlignment(Element.ALIGN_CENTER);
            nameCell.addElement(namePara);

            Paragraph mottoPara = new Paragraph(ORG_MOTTO, mottoFont);
            mottoPara.setAlignment(Element.ALIGN_CENTER);
            nameCell.addElement(mottoPara);
            top.addCell(nameCell);

            PdfPCell sysCell = new PdfPCell();
            sysCell.setBackgroundColor(BRAND_DARK);
            sysCell.setPadding(5);
            sysCell.setBorder(Rectangle.NO_BORDER);
            sysCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sysCell.addElement(new Paragraph(SYS_NAME, sysNameFont));
            top.addCell(sysCell);

            doc.add(top);

            // Contact strip
            PdfPTable contact = new PdfPTable(3);
            contact.setWidthPercentage(100);
            contact.setSpacingAfter(14);
            contact.setSpacingBefore(0);
            String[] contactItems = {
                ORG_ADDRESS + "  ·  " + ORG_PHONE,
                ORG_EMAIL,
                ORG_WEB,
            };
            for (String item : contactItems) {
                PdfPCell c = new PdfPCell(new Phrase(item, metaFont));
                c.setBorder(Rectangle.NO_BORDER);
                c.setBackgroundColor(new Color(0xF9, 0xF9, 0xF9));
                c.setPadding(4);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                contact.addCell(c);
            }
            doc.add(contact);

            // Report title
            Paragraph rTitle = new Paragraph(subtitle.toUpperCase(), titleFont);
            rTitle.setAlignment(Element.ALIGN_CENTER);
            rTitle.setSpacingBefore(4);
            rTitle.setSpacingAfter(4);
            doc.add(rTitle);

            // Orange separator line
            PdfPTable sep = new PdfPTable(1);
            sep.setWidthPercentage(60);
            sep.setSpacingAfter(8);
            PdfPCell sepCell = new PdfPCell();
            sepCell.setBackgroundColor(BRAND);
            sepCell.setFixedHeight(2f);
            sepCell.setBorder(Rectangle.NO_BORDER);
            sep.addCell(sepCell);
            doc.add(sep);

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
            Paragraph notes = new Paragraph(text, metaFont);
            notes.setSpacingBefore(20);
            notes.setSpacingAfter(6);
            doc.add(notes);

            // Thin orange separator
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            line.setSpacingBefore(14);
            line.setSpacingAfter(0);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBackgroundColor(BRAND);
            lineCell.setFixedHeight(1.5f);
            lineCell.setBorder(Rectangle.NO_BORDER);
            line.addCell(lineCell);
            doc.add(line);

            // Footer contact band
            PdfPTable footBand = new PdfPTable(1);
            footBand.setWidthPercentage(100);
            String footText = ORG_NAME + "  ·  " + ORG_ADDRESS + "  ·  " + ORG_PHONE + "  ·  " + ORG_WEB;
            PdfPCell footCell = new PdfPCell(new Phrase(footText, footerFont));
            footCell.setBackgroundColor(BRAND_DARK);
            footCell.setPadding(6);
            footCell.setBorder(Rectangle.NO_BORDER);
            footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footBand.addCell(footCell);
            doc.add(footBand);

            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF footer", e);
        }
    }

    public byte[] build() {
        doc.close();
        return out.toByteArray();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Paragraph sectionTitle(String text) {
        Paragraph p = new Paragraph(text, sectionFont);
        p.setSpacingBefore(16);
        p.setSpacingAfter(6);
        return p;
    }

    private PdfPTable kvTable(String[][] rows) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try { table.setWidths(new float[]{2.5f, 1f}); } catch (Exception ignored) {}
        boolean shaded = false;
        for (String[] row : rows) {
            PdfPCell labelCell = new PdfPCell(new Phrase(row[0], labelFont));
            PdfPCell valueCell = new PdfPCell(new Phrase(row.length > 1 ? row[1] : "-", valueFont));
            for (PdfPCell c : new PdfPCell[]{labelCell, valueCell}) {
                c.setPadding(6);
                c.setBorderColor(BORDER);
                c.setBackgroundColor(shaded ? BRAND_LIGHT : Color.WHITE);
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
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        if (rows == null || rows.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase(emptyMessage, tableCellFont));
            empty.setColspan(headers.length);
            empty.setPadding(10);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
            return table;
        }

        boolean shaded = false;
        for (String[] row : rows) {
            for (String v : row) {
                PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "-", tableCellFont));
                cell.setPadding(5);
                cell.setBorderColor(BORDER);
                cell.setBackgroundColor(shaded ? BRAND_LIGHT : Color.WHITE);
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

    public static String nullSafe(String s) { return s != null ? s : "-"; }
}

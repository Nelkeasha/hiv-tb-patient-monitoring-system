package com.nelly.hivtbmonitoringsystem.service.export.support;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PdfReportBuilder {

    // ── DMC brand palette — exact pixel values from dmc-logo.png ────────────
    /** Primary logo orange  #E74A2E */
    public static final Color BRAND       = new Color(0xE7, 0x4A, 0x2E);
    /** Dark logo brown (bottom bar of logo)  #853C30 */
    public static final Color BRAND_DARK  = new Color(0x85, 0x3C, 0x30);
    /** Very light orange tint for alternating rows  #FDE8E4 */
    public static final Color BRAND_LIGHT = new Color(0xFD, 0xE8, 0xE4);
    /** Near-black body text  #2C2C2C */
    public static final Color TEXT_DARK   = new Color(0x2C, 0x2C, 0x2C);
    /** Mid-grey secondary text  #6B7280 */
    public static final Color TEXT_GREY   = new Color(0x6B, 0x72, 0x80);
    /** Light border colour  #E9E9E9 */
    public static final Color BORDER      = new Color(0xE9, 0xE9, 0xE9);

    // ── Letterhead strings (from official DMC letterhead) ────────────────────
    private static final String ORG_FULL    = "DREAM MEDICAL CENTER (DMC) HOSPITAL";
    private static final String ORG_MOTTO   = "\"Where Science and Faith meet to bring healing\"";
    private static final String ORG_ADDRESS = "P.O.Box 6737 Kigali, KK 541 St Kagarama, Kicukiro-Bugesera Rd";
    private static final String ORG_TEL     = "Tel: +250 782 749 660 / +250 783 942 211";
    private static final String ORG_EMAIL   = "info@dreammedicalcenter.rw";
    private static final String ORG_WEB     = "www.dreammedicalcenter.rw";
    private static final String SYS_NAME    = "HIV/TB Patient Monitoring System";

    // ── Font definitions ─────────────────────────────────────────────────────
    private final Font orgNameFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BRAND);
    private final Font mottoFont     = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7, TEXT_GREY);
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
            // ── Top orange stripe (thin) ─────────────────────────────────────
            PdfPTable topStripe = new PdfPTable(1);
            topStripe.setWidthPercentage(100);
            topStripe.setSpacingAfter(0);
            PdfPCell stripe = new PdfPCell();
            stripe.setBackgroundColor(BRAND);
            stripe.setFixedHeight(4f);
            stripe.setBorder(Rectangle.NO_BORDER);
            topStripe.addCell(stripe);
            doc.add(topStripe);

            // ── Logo + org identity row ─────────────────────────────────────
            PdfPTable logoRow = new PdfPTable(new float[]{1f, 2f});
            logoRow.setWidthPercentage(100);
            logoRow.setSpacingAfter(0);
            logoRow.setSpacingBefore(0);

            // Logo cell (left)
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setPadding(8);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            try (InputStream is = getClass().getResourceAsStream("/static/dmc-logo.png")) {
                if (is != null) {
                    Image logo = Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(130, 54);
                    logo.setAlignment(Element.ALIGN_LEFT);
                    logoCell.addElement(logo);
                } else {
                    // Fallback: text-only if logo resource missing
                    logoCell.addElement(new Paragraph(ORG_FULL, orgNameFont));
                }
            } catch (Exception ignored) {
                logoCell.addElement(new Paragraph(ORG_FULL, orgNameFont));
            }
            logoRow.addCell(logoCell);

            // Org details cell (right)
            PdfPCell detailCell = new PdfPCell();
            detailCell.setBorder(Rectangle.NO_BORDER);
            detailCell.setPadding(8);
            detailCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Paragraph orgFull = new Paragraph(ORG_FULL, orgNameFont);
            orgFull.setAlignment(Element.ALIGN_RIGHT);
            detailCell.addElement(orgFull);

            Paragraph motto = new Paragraph(ORG_MOTTO, mottoFont);
            motto.setAlignment(Element.ALIGN_RIGHT);
            detailCell.addElement(motto);

            Paragraph contact = new Paragraph(ORG_ADDRESS + "\n" + ORG_TEL + "  ·  " + ORG_EMAIL + "  ·  " + ORG_WEB, metaFont);
            contact.setAlignment(Element.ALIGN_RIGHT);
            contact.setSpacingBefore(3);
            detailCell.addElement(contact);

            logoRow.addCell(detailCell);
            doc.add(logoRow);

            // ── Bottom orange stripe ─────────────────────────────────────────
            PdfPTable botStripe = new PdfPTable(1);
            botStripe.setWidthPercentage(100);
            botStripe.setSpacingAfter(10);
            PdfPCell bot = new PdfPCell();
            bot.setBackgroundColor(BRAND_DARK);
            bot.setFixedHeight(2.5f);
            bot.setBorder(Rectangle.NO_BORDER);
            botStripe.addCell(bot);
            doc.add(botStripe);

            // ── System name sub-header ───────────────────────────────────────
            PdfPTable sysRow = new PdfPTable(1);
            sysRow.setWidthPercentage(100);
            sysRow.setSpacingAfter(12);
            PdfPCell sysCell = new PdfPCell();
            sysCell.setBackgroundColor(BRAND);
            sysCell.setPadding(5);
            sysCell.setBorder(Rectangle.NO_BORDER);
            sysCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sysCell.addElement(new Paragraph(SYS_NAME, sysNameFont));
            sysRow.addCell(sysCell);
            doc.add(sysRow);

            // ── Report title + separator ─────────────────────────────────────
            Paragraph rTitle = new Paragraph(subtitle.toUpperCase(), titleFont);
            rTitle.setAlignment(Element.ALIGN_CENTER);
            rTitle.setSpacingBefore(4);
            rTitle.setSpacingAfter(4);
            doc.add(rTitle);

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
            String footText = ORG_FULL + "  ·  " + ORG_ADDRESS + "  ·  " + ORG_TEL + "  ·  " + ORG_WEB;
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

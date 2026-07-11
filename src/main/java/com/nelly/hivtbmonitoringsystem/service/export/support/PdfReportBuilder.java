package com.nelly.hivtbmonitoringsystem.service.export.support;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.nelly.hivtbmonitoringsystem.dto.report.Indicator;
import com.nelly.hivtbmonitoringsystem.dto.report.Recommendation;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PdfReportBuilder {

    // ── DMC brand palette — from official DMC letterhead HTML reference ──────
    public static final Color BRAND       = new Color(0xE0, 0x70, 0x4D); // #E0704D coral-orange
    public static final Color BRAND_DEEP  = new Color(0xC9, 0x55, 0x2F); // #C9552F terracotta
    public static final Color BRAND_BROWN = new Color(0x5E, 0x2A, 0x1E); // #5E2A1E deep brown
    public static final Color TINT        = new Color(0xFB, 0xED, 0xE7); // #FBEDE7 table header
    public static final Color ALT_ROW     = new Color(0xFB, 0xFB, 0xFB); // #FBFBFB alt rows
    public static final Color TEXT_DARK   = new Color(0x2C, 0x2C, 0x2C); // #2C2C2C
    public static final Color TEXT_GREY   = new Color(0x6B, 0x72, 0x80); // #6B7280
    public static final Color BORDER      = new Color(0xE2, 0xE2, 0xE2); // #E2E2E2
    public static final Color SUCCESS     = new Color(0x2E, 0x7D, 0x32); // #2E7D32
    public static final Color WARNING_CLR = new Color(0xB2, 0x6A, 0x00); // #B26A00
    public static final Color DANGER      = new Color(0xC0, 0x39, 0x2B); // #C0392B

    // ── Organisation strings ─────────────────────────────────────────────────
    private static final String ORG_FULL    = "Dream Medical Center (DMC) Hospital";
    private static final String ORG_MOTTO   = "“Where Science and Faith meet to bring healing”";
    private static final String ORG_ADDRESS = "P.O. Box 6737 Kigali · KK 541 St Kagarama, Kicukiro–Bugesera Rd";
    private static final String ORG_TEL     = "+250 782 749 660 / +250 783 942 211";
    private static final String ORG_EMAIL   = "info@dreammedicalcenter.rw";
    private static final String ORG_WEB     = "www.dreammedicalcenter.rw";
    private static final String SYS_NAME    = "HIV / TB Patient Monitoring System";

    // ── Fonts ────────────────────────────────────────────────────────────────
    private final Font orgNameFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  13f, BRAND_DEEP);
    private final Font sysNameFont   = FontFactory.getFont(FontFactory.HELVETICA,         8f, TEXT_GREY);
    private final Font mottoFont     = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8f, BRAND_BROWN);
    private final Font titleFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   13f, TEXT_DARK);
    private final Font metaValFont   = FontFactory.getFont(FontFactory.HELVETICA,         8f, TEXT_GREY);
    private final Font metaKeyFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    8f, TEXT_DARK);
    private final Font sectionFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   10f, BRAND_DEEP);
    private final Font labelFont     = FontFactory.getFont(FontFactory.HELVETICA,        8.5f, TEXT_GREY);
    private final Font valueFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD,  8.5f, TEXT_DARK);
    private final Font tableHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    8f, BRAND_BROWN);
    private final Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA,         8f, TEXT_DARK);
    private final Font sigNameFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    9f, TEXT_DARK);
    private final Font sigRoleFont   = FontFactory.getFont(FontFactory.HELVETICA,         8f, TEXT_GREY);
    private final Font noteFont      = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8f, TEXT_GREY);
    private final Font execLeadFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   9f, BRAND_DEEP);
    private final Font execBodyFont  = FontFactory.getFont(FontFactory.HELVETICA,         9f, TEXT_DARK);
    private final Font kpiLabelFont  = FontFactory.getFont(FontFactory.HELVETICA,       7.5f, TEXT_GREY);
    private final Font kpiValueFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   16f, BRAND_BROWN);
    private final Font kpiTargetFont = FontFactory.getFont(FontFactory.HELVETICA,       6.8f, TEXT_GREY);

    private final Document doc;
    private final ByteArrayOutputStream out;
    private final PdfWriter writer;

    public PdfReportBuilder() {
        try {
            this.out    = new ByteArrayOutputStream();
            this.doc    = new Document(PageSize.A4, 44f, 44f, 52f, 72f);
            this.writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new FooterPageEvent());
            doc.open();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise PDF document", e);
        }
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * @param reportTitle Report name shown large on the right (e.g. "Official Facility Clinical Report")
     * @param metaLine    Meta text; pipe-separated segments are rendered on separate lines
     *                    (e.g. "Dream Medical Center — Kigali | Generated: 26 Jun 2026, 09:14")
     */
    public PdfReportBuilder header(String reportTitle, String metaLine) {
        try {
            // 2-column letterhead: left = logo + brand text  |  right = doc-meta
            PdfPTable hdr = new PdfPTable(new float[]{1.2f, 1f});
            hdr.setWidthPercentage(100);
            hdr.setSpacingAfter(0);

            // ── Left cell: real logo + sub-brand text ──────────────────────
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setPadding(6);
            leftCell.setVerticalAlignment(Element.ALIGN_TOP);

            boolean logoLoaded = false;
            try (InputStream is = getClass().getResourceAsStream("/static/dmc-logo.png")) {
                if (is != null) {
                    Image logo = Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(148, 61);
                    logo.setAlignment(Element.ALIGN_LEFT);
                    leftCell.addElement(logo);
                    logoLoaded = true;
                }
            } catch (Exception ignored) {}

            if (!logoLoaded) {
                Paragraph fallback = new Paragraph(ORG_FULL, orgNameFont);
                leftCell.addElement(fallback);
            }

            Paragraph sub = new Paragraph(SYS_NAME, sysNameFont);
            sub.setSpacingBefore(4);
            leftCell.addElement(sub);

            Paragraph motto = new Paragraph(ORG_MOTTO, mottoFont);
            motto.setSpacingBefore(2);
            leftCell.addElement(motto);

            hdr.addCell(leftCell);

            // ── Right cell: report title + meta block ──────────────────────
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setPadding(6);
            rightCell.setVerticalAlignment(Element.ALIGN_TOP);

            Paragraph rTitle = new Paragraph(reportTitle.toUpperCase(), titleFont);
            rTitle.setAlignment(Element.ALIGN_RIGHT);
            rTitle.setSpacingAfter(7);
            rightCell.addElement(rTitle);

            if (metaLine != null) {
                for (String seg : metaLine.split("\\|")) {
                    String trimmed = seg.trim();
                    if (!trimmed.isEmpty()) {
                        Paragraph mPara = new Paragraph(trimmed, metaValFont);
                        mPara.setAlignment(Element.ALIGN_RIGHT);
                        rightCell.addElement(mPara);
                    }
                }
            }

            hdr.addCell(rightCell);
            doc.add(hdr);

            // ── Terracotta band + thin rule ───────────────────────────────
            addBand(BRAND_DEEP, 5f, 8f, 0f);
            addBand(BORDER,     1f, 0f, 14f);

            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF header", e);
        }
    }

    public PdfReportBuilder section(String title, String[][] kvRows) {
        try {
            addSectionTitle(title);
            doc.add(kvTable(kvRows));
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF section: " + title, e);
        }
    }

    public PdfReportBuilder dataTable(String title, String[] headers, List<String[]> rows, String emptyMessage) {
        try {
            addSectionTitle(title);
            doc.add(buildDataTable(headers, rows, emptyMessage));
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF table: " + title, e);
        }
    }

    /** Renders a clinical sign-off block with two signature lines and a stamp box. */
    public PdfReportBuilder signOff(String preparedBy, String reviewedBy) {
        try {
            PdfPTable sigTable = new PdfPTable(new float[]{1f, 1f, 0.55f});
            sigTable.setWidthPercentage(100);
            sigTable.setSpacingBefore(28);

            sigTable.addCell(buildSigCol("Prepared by", preparedBy));
            sigTable.addCell(buildSigCol("Reviewed by", reviewedBy));

            // Stamp box
            PdfPCell stampOuter = new PdfPCell();
            stampOuter.setBorder(Rectangle.NO_BORDER);
            stampOuter.setPaddingLeft(12);

            PdfPTable stampBox = new PdfPTable(1);
            stampBox.setWidthPercentage(100);
            PdfPCell box = new PdfPCell(new Phrase("Official DMC Stamp",
                    FontFactory.getFont(FontFactory.HELVETICA, 8f, new Color(0xCF, 0xCF, 0xCF))));
            box.setFixedHeight(76);
            box.setBorderColor(BORDER);
            box.setBorderWidth(1.2f);
            box.setHorizontalAlignment(Element.ALIGN_CENTER);
            box.setVerticalAlignment(Element.ALIGN_MIDDLE);
            stampBox.addCell(box);
            stampOuter.addElement(stampBox);
            sigTable.addCell(stampOuter);

            doc.add(sigTable);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add sign-off", e);
        }
    }

    /**
     * Adds an optional notes paragraph before the document is closed.
     * The actual footer band (contact, confidential, page number) is rendered
     * automatically on every page by FooterPageEvent.
     */
    public PdfReportBuilder footer(String notes) {
        try {
            if (notes != null && !notes.isEmpty()) {
                Paragraph p = new Paragraph(notes, noteFont);
                p.setSpacingBefore(20);
                p.setSpacingAfter(4);
                doc.add(p);
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add PDF footer notes", e);
        }
    }

    public byte[] build() {
        doc.close();
        return out.toByteArray();
    }

    /**
     * Executive-summary block: a run of justified paragraphs. Each entry is
     * "lead||body" — the lead-in (e.g. "What happened.") is bolded in the brand
     * colour, the body rendered as normal justified prose.
     */
    public PdfReportBuilder executiveSummary(List<String> points) {
        try {
            addSectionTitle("Executive Summary");
            if (points != null) {
                for (String point : points) {
                    if (point == null || point.isBlank()) continue;
                    String lead;
                    String body;
                    int sep = point.indexOf("||");
                    if (sep >= 0) {
                        lead = point.substring(0, sep).trim();
                        body = point.substring(sep + 2).trim();
                    } else {
                        lead = "";
                        body = point.trim();
                    }
                    Paragraph p = new Paragraph();
                    p.setAlignment(Element.ALIGN_JUSTIFIED);
                    p.setSpacingAfter(7);
                    p.setLeading(12.5f);
                    if (!lead.isEmpty()) {
                        p.add(new Chunk(lead + "  ", execLeadFont));
                    }
                    p.add(new Chunk(body, execBodyFont));
                    doc.add(p);
                }
            }
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add executive summary", e);
        }
    }

    /**
     * KPI card grid (4 per row): a big value, an optional coloured delta chip
     * comparing to the previous period, and a target/context caption.
     */
    public PdfReportBuilder kpiCards(String title, List<Indicator> indicators) {
        try {
            addSectionTitle(title);
            if (indicators == null || indicators.isEmpty()) return this;

            final int cols = 4;
            PdfPTable table = new PdfPTable(cols);
            table.setWidthPercentage(100);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            for (Indicator ind : indicators) {
                table.addCell(buildKpiCard(ind));
            }
            // pad the last row so all cells are equal width
            int remainder = indicators.size() % cols;
            if (remainder != 0) {
                for (int i = 0; i < cols - remainder; i++) {
                    PdfPCell empty = new PdfPCell();
                    empty.setBorder(Rectangle.NO_BORDER);
                    table.addCell(empty);
                }
            }
            table.setSpacingBefore(2);
            doc.add(table);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add KPI cards", e);
        }
    }

    /**
     * Recommended-actions table: numbered, colour-coded priority tag, finding,
     * suggested action, and the owner responsible.
     */
    public PdfReportBuilder recommendations(List<Recommendation> recs) {
        try {
            addSectionTitle("Recommended Actions");
            PdfPTable table = new PdfPTable(new float[]{0.4f, 1.1f, 3.2f, 3.5f, 1.3f});
            table.setWidthPercentage(100);

            for (String h : new String[]{"#", "Priority", "Finding", "Suggested Action", "Owner"}) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeadFont));
                cell.setBackgroundColor(TINT);
                cell.setPadding(7);
                cell.setBorderColor(BORDER);
                table.addCell(cell);
            }

            if (recs == null || recs.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No recommendations generated", tableCellFont));
                empty.setColspan(5);
                empty.setPadding(10);
                empty.setBorderColor(BORDER);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(empty);
                doc.add(table);
                return this;
            }

            int i = 1;
            boolean alt = false;
            for (Recommendation r : recs) {
                Color bg = alt ? ALT_ROW : Color.WHITE;

                PdfPCell num = new PdfPCell(new Phrase(String.valueOf(i++), tableCellFont));
                num.setPadding(6); num.setBorderColor(BORDER); num.setBackgroundColor(bg);
                num.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(num);

                Color sevColor = severityColor(r.getSeverity());
                Font tagFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, Color.WHITE);
                PdfPCell tag = new PdfPCell(new Phrase(r.getSeverity().name(), tagFont));
                tag.setBackgroundColor(sevColor);
                tag.setPadding(6); tag.setBorder(Rectangle.NO_BORDER);
                tag.setHorizontalAlignment(Element.ALIGN_CENTER);
                tag.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(tag);

                table.addCell(bodyCell(nullSafe(r.getFinding()), bg));
                table.addCell(bodyCell(nullSafe(r.getAction()), bg));
                table.addCell(bodyCell(nullSafe(r.getOwner()), bg));
                alt = !alt;
            }
            doc.add(table);
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add recommendations", e);
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private PdfPCell buildKpiCard(Indicator ind) {
        PdfPCell card = new PdfPCell();
        card.setPadding(9);
        card.setBorderColor(BORDER);
        card.setBorderWidth(0.8f);
        card.setBackgroundColor(Color.WHITE);
        card.setVerticalAlignment(Element.ALIGN_TOP);

        Paragraph label = new Paragraph(ind.getLabel() != null ? ind.getLabel().toUpperCase() : "", kpiLabelFont);
        label.setLeading(9f);
        card.addElement(label);

        Paragraph value = new Paragraph(ind.getValue() != null ? ind.getValue() : "—", kpiValueFont);
        value.setSpacingBefore(3);
        value.setLeading(17f);
        card.addElement(value);

        if (ind.getDeltaLabel() != null) {
            Color dc = ragColor(ind.getStatus());
            Font deltaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, dc);
            // Signed delta text ("+4.2 pts" / "-8") + colour convey direction;
            // avoid Unicode arrow glyphs (not in the PDF's WINANSI encoding).
            Paragraph delta = new Paragraph(ind.getDeltaLabel(), deltaFont);
            delta.setSpacingBefore(2);
            card.addElement(delta);
        }

        if (ind.getTarget() != null) {
            Paragraph target = new Paragraph(ind.getTarget(), kpiTargetFont);
            target.setSpacingBefore(2);
            card.addElement(target);
        }
        return card;
    }

    private PdfPCell bodyCell(String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, tableCellFont));
        cell.setPadding(6);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(bg);
        return cell;
    }

    private static Color severityColor(Recommendation.Severity sev) {
        if (sev == null) return TEXT_GREY;
        return switch (sev) {
            case CRITICAL -> DANGER;
            case WARNING -> WARNING_CLR;
            case INFO -> TEXT_GREY;
        };
    }

    private static Color ragColor(Indicator.Rag rag) {
        if (rag == null) return TEXT_GREY;
        return switch (rag) {
            case GOOD -> SUCCESS;
            case WATCH -> WARNING_CLR;
            case BAD -> DANGER;
            case NEUTRAL -> TEXT_GREY;
        };
    }


    private void addBand(Color color, float height, float spacingBefore, float spacingAfter)
            throws DocumentException {
        PdfPTable band = new PdfPTable(1);
        band.setWidthPercentage(100);
        band.setSpacingBefore(spacingBefore);
        band.setSpacingAfter(spacingAfter);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(color);
        cell.setFixedHeight(height);
        cell.setBorder(Rectangle.NO_BORDER);
        band.addCell(cell);
        doc.add(band);
    }

    private void addSectionTitle(String text) throws DocumentException {
        Paragraph p = new Paragraph(text.toUpperCase(), sectionFont);
        p.setSpacingBefore(16);
        p.setSpacingAfter(4);
        doc.add(p);
        addBand(BORDER, 1f, 0f, 6f);
    }

    /** Two-column definition grid: key (muted, left) | value (bold, right) */
    private PdfPTable kvTable(String[][] rows) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try { table.setWidths(new float[]{2.5f, 1f}); } catch (Exception ignored) {}
        boolean alt = false;
        for (String[] row : rows) {
            PdfPCell keyCell = new PdfPCell(new Phrase(row[0], labelFont));
            PdfPCell valCell = new PdfPCell(new Phrase(row.length > 1 ? row[1] : "—", valueFont));
            for (PdfPCell c : new PdfPCell[]{keyCell, valCell}) {
                c.setPadding(5.5f);
                c.setBorderColor(BORDER);
                c.setBackgroundColor(alt ? TINT : Color.WHITE);
            }
            valCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(keyCell);
            table.addCell(valCell);
            alt = !alt;
        }
        return table;
    }

    /** Data table: TINT header row with BRAND_BROWN bold text; alternating body rows */
    private PdfPTable buildDataTable(String[] headers, List<String[]> rows, String emptyMessage) {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        // Header row
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, tableHeadFont));
            cell.setBackgroundColor(TINT);
            cell.setPadding(7);
            cell.setBorderColor(BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }

        if (rows == null || rows.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase(emptyMessage, tableCellFont));
            empty.setColspan(headers.length);
            empty.setPadding(10);
            empty.setBorderColor(BORDER);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
            return table;
        }

        boolean alt = false;
        for (String[] row : rows) {
            for (String v : row) {
                PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "—", tableCellFont));
                cell.setPadding(6);
                cell.setBorderColor(BORDER);
                cell.setBackgroundColor(alt ? ALT_ROW : Color.WHITE);
                table.addCell(cell);
            }
            alt = !alt;
        }
        return table;
    }

    private PdfPCell buildSigCol(String label, String nameAndRole) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingRight(14);

        // Space above signature line
        Paragraph space = new Paragraph(" ");
        space.setSpacingBefore(36);
        cell.addElement(space);

        // Signature rule
        PdfPTable rule = new PdfPTable(1);
        rule.setWidthPercentage(100);
        PdfPCell ruleLine = new PdfPCell();
        ruleLine.setFixedHeight(1.4f);
        ruleLine.setBackgroundColor(TEXT_DARK);
        ruleLine.setBorder(Rectangle.NO_BORDER);
        rule.addCell(ruleLine);
        cell.addElement(rule);

        // Label
        Paragraph lbl = new Paragraph(label + ":", sigRoleFont);
        lbl.setSpacingBefore(4);
        cell.addElement(lbl);

        // Name / role string
        if (nameAndRole != null && !nameAndRole.isEmpty()) {
            cell.addElement(new Paragraph(nameAndRole, sigNameFont));
        } else {
            Paragraph blank = new Paragraph("____________________", sigRoleFont);
            cell.addElement(blank);
        }
        return cell;
    }

    // ── Static utilities ─────────────────────────────────────────────────────

    public static String formatPct(BigDecimal pct) {
        if (pct == null) return "0%";
        return pct.setScale(1, RoundingMode.HALF_UP) + "%";
    }

    public static String nullSafe(String s) { return s != null ? s : "—"; }

    // ── Page footer event ────────────────────────────────────────────────────

    /** Renders the contact-info footer band + page number on every page. */
    private class FooterPageEvent extends PdfPageEventHelper {

        private PdfTemplate totalPagesTemplate;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPagesTemplate = writer.getDirectContent().createTemplate(28, 10);
        }

        @Override
        public void onEndPage(PdfWriter w, Document d) {
            try {
                PdfContentByte cb = w.getDirectContent();
                float pageW  = d.getPageSize().getWidth();
                float left   = d.leftMargin();
                float usable = pageW - d.leftMargin() - d.rightMargin();
                float bandY  = d.bottomMargin() - 46f;

                // Terracotta band
                cb.setColorFill(BRAND_DEEP);
                cb.rectangle(left, bandY + 24f, usable, 4f);
                cb.fill();

                // Contact line
                Font fContact = FontFactory.getFont(FontFactory.HELVETICA, 7f, TEXT_GREY);
                String contact = ORG_FULL + "  ·  " + ORG_ADDRESS
                        + "  ·  " + ORG_TEL + "  ·  " + ORG_WEB;
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase(contact, fContact), left + usable / 2f, bandY + 15f, 0);

                // Confidential line
                Font fConf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7f, BRAND_DEEP);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("CONFIDENTIAL — Patient Health Information", fConf),
                        left + usable / 2f, bandY + 5f, 0);

                // Page number (left-aligned total-pages template, right-aligned page)
                Font fPage = FontFactory.getFont(FontFactory.HELVETICA, 7f, TEXT_GREY);
                Phrase pagePhrase = new Phrase("Page " + w.getPageNumber() + " of ", fPage);
                ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                        pagePhrase, left + usable - 20f, bandY + 5f, 0);
                cb.addTemplate(totalPagesTemplate, left + usable - 20f, bandY + 5f);

            } catch (Exception ignored) {}
        }

        @Override
        public void onCloseDocument(PdfWriter w, Document d) {
            try {
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
                totalPagesTemplate.beginText();
                totalPagesTemplate.setFontAndSize(bf, 7f);
                totalPagesTemplate.setColorFill(TEXT_GREY);
                totalPagesTemplate.setTextMatrix(0, 1f);
                totalPagesTemplate.showText(String.valueOf(w.getPageNumber() - 1));
                totalPagesTemplate.endText();
            } catch (Exception ignored) {}
        }
    }
}

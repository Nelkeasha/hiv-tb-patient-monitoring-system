package com.nelly.hivtbmonitoringsystem.service.export;

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
import com.nelly.hivtbmonitoringsystem.dto.response.ChwPerformanceRow;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Renders the facility (clinical) report as a formal PDF document — the
 * "official" artifact clinical staff print or file, as opposed to the live
 * dashboard which only shows a subset of these figures.
 */
@Service
public class ClinicalPdfReportService {

    private static final Color BRAND = new Color(0x00, 0x6D, 0x77);
    private static final Color LIGHT = new Color(0xED, 0xF6, 0xF9);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public byte[] generate(FacilityReportResponse report) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 42, 42, 56, 48);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BRAND);
            Font subtitle = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
            Font meta = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
            Font section = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BRAND);

            Paragraph header = new Paragraph("HIV/TB MONITORING SYSTEM", title);
            header.setAlignment(Element.ALIGN_CENTER);
            doc.add(header);

            Paragraph sub = new Paragraph("Official Facility Clinical Report", subtitle);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(4);
            doc.add(sub);

            Paragraph metaLine = new Paragraph(
                    (report.getFacilityName() != null ? report.getFacilityName() : "Facility")
                            + (report.getDistrict() != null ? " — " + report.getDistrict() : "")
                            + "   |   Generated: "
                            + (report.getGeneratedAt() != null ? report.getGeneratedAt().format(TS) : "-"),
                    meta);
            metaLine.setAlignment(Element.ALIGN_CENTER);
            metaLine.setSpacingAfter(18);
            doc.add(metaLine);

            doc.add(sectionTitle("Patient Overview", section));
            doc.add(kvTable(new String[][]{
                    {"Total Active Patients", String.valueOf(report.getTotalActivePatients())},
                    {"HIV Only", String.valueOf(report.getHivOnly())},
                    {"TB Only", String.valueOf(report.getTbOnly())},
                    {"HIV + TB Co-infection", String.valueOf(report.getHivTbCoinfection())},
            }));

            doc.add(sectionTitle("Risk Distribution", section));
            doc.add(kvTable(new String[][]{
                    {"Low", String.valueOf(report.getRiskLow())},
                    {"Moderate", String.valueOf(report.getRiskModerate())},
                    {"High", String.valueOf(report.getRiskHigh())},
                    {"Critical", String.valueOf(report.getRiskCritical())},
                    {"Unscored", String.valueOf(report.getRiskUnscored())},
            }));

            doc.add(sectionTitle("Adherence", section));
            doc.add(kvTable(new String[][]{
                    {"Facility Adherence Average", formatPct(report.getFacilityAdherenceAvg())},
                    {"Below Threshold (Patients)", String.valueOf(report.getBelowThresholdCount())},
                    {"False Confirmation Flags", String.valueOf(report.getFalseConfirmationFlagCount())},
            }));

            doc.add(sectionTitle("Referrals", section));
            doc.add(kvTable(new String[][]{
                    {"Total", String.valueOf(report.getReferralTotal())},
                    {"Pending", String.valueOf(report.getReferralPending())},
                    {"Confirmed", String.valueOf(report.getReferralConfirmed())},
                    {"Attended", String.valueOf(report.getReferralAttended())},
                    {"Not Attended", String.valueOf(report.getReferralNotAttended())},
                    {"Cancelled", String.valueOf(report.getReferralCancelled())},
            }));

            doc.add(sectionTitle("Alerts (Unresolved)", section));
            doc.add(kvTable(new String[][]{
                    {"Total Unresolved", String.valueOf(report.getUnresolvedAlerts())},
                    {"Critical", String.valueOf(report.getCriticalAlerts())},
                    {"Warning", String.valueOf(report.getWarningAlerts())},
            }));

            doc.add(sectionTitle("CHW Performance (Last 30 Days)", section));
            doc.add(chwPerformanceTable(report));

            Paragraph footer = new Paragraph(
                    "This is a system-generated report from the HIV/TB Monitoring System. "
                            + "Figures reflect data as of the generation timestamp above.",
                    meta);
            footer.setSpacingBefore(20);
            doc.add(footer);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private Paragraph sectionTitle(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
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
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
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

    private PdfPTable chwPerformanceTable(FacilityReportResponse report) {
        String[] headers = {"CHW Name", "Employee Code", "Village", "Active Patients", "Visits (30d)", "Missed Doses (30d)"};
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setBackgroundColor(BRAND);
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        if (report.getChwPerformance() == null || report.getChwPerformance().isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("No CHW activity recorded in the last 30 days", cellFont));
            empty.setColspan(headers.length);
            empty.setPadding(8);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
            return table;
        }

        boolean shaded = false;
        for (ChwPerformanceRow row : report.getChwPerformance()) {
            String[] values = {
                    nullSafe(row.getChwName()),
                    nullSafe(row.getEmployeeCode()),
                    nullSafe(row.getAssignedVillage()),
                    String.valueOf(row.getActivePatients()),
                    String.valueOf(row.getVisitsLast30Days()),
                    String.valueOf(row.getMissedDosesLast30Days()),
            };
            for (String v : values) {
                PdfPCell cell = new PdfPCell(new Phrase(v, cellFont));
                cell.setPadding(5);
                cell.setBackgroundColor(shaded ? LIGHT : Color.WHITE);
                table.addCell(cell);
            }
            shaded = !shaded;
        }
        return table;
    }

    private String nullSafe(String s) {
        return s != null ? s : "-";
    }

    private String formatPct(java.math.BigDecimal pct) {
        if (pct == null) return "0%";
        return pct.setScale(1, java.math.RoundingMode.HALF_UP) + "%";
    }
}

package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportRow;
import com.nelly.hivtbmonitoringsystem.service.export.support.PdfReportBuilder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the system-wide admin report as a formal PDF — the printable
 * counterpart to the Excel workbook, for admins who want one document for a
 * program review instead of a spreadsheet.
 */
@Service
public class AdminPdfReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public byte[] generate(AdminReportResponse report) {
        String metaLine = "System-Wide   |   Generated: "
                + (report.getGeneratedAt() != null ? report.getGeneratedAt().format(TS) : "-");

        PdfReportBuilder builder = new PdfReportBuilder()
                .header("System-Wide Administrative Report", metaLine)
                .section("Users & Workforce", new String[][]{
                        {"Total Users", String.valueOf(report.getTotalUsers())},
                        {"Active Users", String.valueOf(report.getActiveUsers())},
                        {"Inactive Users", String.valueOf(report.getInactiveUsers())},
                        {"Community Health Workers", String.valueOf(report.getTotalChw())},
                        {"Facility Providers", String.valueOf(report.getTotalProviders())},
                        {"Supervisors", String.valueOf(report.getTotalSupervisors())},
                        {"Patient Accounts", String.valueOf(report.getTotalPatients())},
                })
                .section("Facilities", new String[][]{
                        {"Total Facilities", String.valueOf(report.getTotalFacilities())},
                        {"Active Facilities", String.valueOf(report.getActiveFacilities())},
                })
                .section("Patients (System-Wide)", new String[][]{
                        {"Total Active Patients", String.valueOf(report.getTotalActivePatients())},
                        {"HIV Only", String.valueOf(report.getHivOnly())},
                        {"TB Only", String.valueOf(report.getTbOnly())},
                        {"HIV + TB Co-infection", String.valueOf(report.getHivTbCoinfection())},
                })
                .section("Risk Distribution", new String[][]{
                        {"Low", String.valueOf(report.getRiskLow())},
                        {"Moderate", String.valueOf(report.getRiskModerate())},
                        {"High", String.valueOf(report.getRiskHigh())},
                        {"Critical", String.valueOf(report.getRiskCritical())},
                        {"Unscored", String.valueOf(report.getRiskUnscored())},
                })
                .section("Adherence", new String[][]{
                        {"System Adherence Average", PdfReportBuilder.formatPct(report.getSystemAdherenceAvg())},
                        {"Below Threshold (Patients)", String.valueOf(report.getBelowThresholdCount())},
                        {"False Confirmation Flags", String.valueOf(report.getFalseConfirmationFlagCount())},
                })
                .section("Alerts (Unresolved)", new String[][]{
                        {"Total Unresolved", String.valueOf(report.getUnresolvedAlerts())},
                        {"Critical", String.valueOf(report.getCriticalAlerts())},
                        {"Warning", String.valueOf(report.getWarningAlerts())},
                        {"Missed Dose", String.valueOf(report.getMissedDoseAlerts())},
                })
                .section("FHIR Sync Status", new String[][]{
                        {"Pending", String.valueOf(report.getFhirSyncPending())},
                        {"Synced", String.valueOf(report.getFhirSyncSynced())},
                        {"Failed", String.valueOf(report.getFhirSyncFailed())},
                })
                .section("LTFU Tracing", new String[][]{
                        {"Active Tasks", String.valueOf(report.getActiveLtfuTasks())},
                        {"Confirmed LTFU", String.valueOf(report.getLtfuConfirmedCount())},
                        {"Escalated", String.valueOf(report.getEscalatedCount())},
                })
                .dataTable("Facility Breakdown",
                        new String[]{"Facility", "District", "Active Patients", "Total CHWs", "Adherence Avg (%)", "High Risk", "Unresolved Alerts"},
                        facilityRows(report),
                        "No facility data available")
                .footer("This is a system-generated report from the HIV/TB Monitoring System. "
                        + "Figures reflect data as of the generation timestamp above.");

        return builder.build();
    }

    private List<String[]> facilityRows(AdminReportResponse report) {
        List<String[]> rows = new ArrayList<>();
        if (report.getFacilityBreakdown() == null) return rows;
        for (FacilityReportRow row : report.getFacilityBreakdown()) {
            rows.add(new String[]{
                    PdfReportBuilder.nullSafe(row.getFacilityName()),
                    PdfReportBuilder.nullSafe(row.getDistrict()),
                    String.valueOf(row.getActivePatients()),
                    String.valueOf(row.getTotalChws()),
                    row.getAdherenceAvg() != null ? PdfReportBuilder.formatPct(row.getAdherenceAvg()) : "-",
                    String.valueOf(row.getHighRiskPatients()),
                    String.valueOf(row.getUnresolvedAlerts()),
            });
        }
        return rows;
    }
}

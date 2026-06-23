package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorChwReportRow;
import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorReportResponse;
import com.nelly.hivtbmonitoringsystem.service.export.support.PdfReportBuilder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the supervisor's programme report as a formal PDF — a printable
 * counterpart to the CSV export, for supervision meetings rather than
 * system integration.
 */
@Service
public class SupervisorPdfReportService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public byte[] generate(SupervisorReportResponse report) {
        String metaLine = (report.getFacilityName() != null ? report.getFacilityName() : "Facility")
                + (report.getDistrict() != null ? " — " + report.getDistrict() : "")
                + "   |   Generated: "
                + (report.getGeneratedAt() != null ? report.getGeneratedAt().format(TS) : "-");

        PdfReportBuilder builder = new PdfReportBuilder()
                .header("Supervisor Programme Report", metaLine)
                .section("Workforce", new String[][]{
                        {"Total CHWs", String.valueOf(report.getTotalChws())},
                        {"Active CHWs", String.valueOf(report.getActiveChws())},
                })
                .section("Patient Overview", new String[][]{
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
                .section("Adherence & Activity", new String[][]{
                        {"Facility Adherence Average", PdfReportBuilder.formatPct(report.getFacilityAdherenceAvg())},
                        {"Below Threshold (Patients)", String.valueOf(report.getBelowThresholdCount())},
                        {"Home Visits (30d)", String.valueOf(report.getTotalHomeVisits30d())},
                        {"Missed Doses (7d)", String.valueOf(report.getTotalMissedDoses7d())},
                })
                .section("Alerts (Unresolved)", new String[][]{
                        {"Total Unresolved", String.valueOf(report.getUnresolvedAlerts())},
                        {"Critical", String.valueOf(report.getCriticalAlerts())},
                        {"Warning", String.valueOf(report.getWarningAlerts())},
                        {"Missed Dose", String.valueOf(report.getMissedDoseAlerts())},
                        {"Early Warning", String.valueOf(report.getEarlyWarningAlerts())},
                })
                .dataTable("CHW Performance (Last 30 Days)",
                        new String[]{"CHW Name", "Employee Code", "Village", "Active Patients", "High Risk", "Visits (30d)", "Missed Doses (7d)"},
                        chwRows(report),
                        "No CHW activity recorded")
                .footer("This is a system-generated report from the HIV/TB Monitoring System. "
                        + "Figures reflect data as of the generation timestamp above.");

        return builder.build();
    }

    private List<String[]> chwRows(SupervisorReportResponse report) {
        List<String[]> rows = new ArrayList<>();
        if (report.getChwPerformance() == null) return rows;
        for (SupervisorChwReportRow row : report.getChwPerformance()) {
            rows.add(new String[]{
                    PdfReportBuilder.nullSafe(row.getChwName()),
                    PdfReportBuilder.nullSafe(row.getEmployeeCode()),
                    PdfReportBuilder.nullSafe(row.getAssignedVillage()),
                    String.valueOf(row.getActivePatients()),
                    String.valueOf(row.getHighRiskPatients()),
                    String.valueOf(row.getHomeVisits30d()),
                    String.valueOf(row.getMissedDoses7d()),
            });
        }
        return rows;
    }
}

package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorChwReportRow;
import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorReportResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Renders the supervisor's CHW-performance report as flat CSV — one row per
 * CHW, no narrative sections — so it can be ingested directly by another
 * system (HMIS import, spreadsheet pivot, analytics pipeline) without a
 * custom parser.
 */
@Service
public class SupervisorCsvReportService {

    private static final String[] HEADERS = {
            "chw_name", "employee_code", "assigned_village",
            "active_patients", "high_risk_patients", "home_visits_30d", "missed_doses_7d",
    };

    public byte[] generate(SupervisorReportResponse report) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", HEADERS)).append("\r\n");

        if (report.getChwPerformance() != null) {
            for (SupervisorChwReportRow row : report.getChwPerformance()) {
                sb.append(csv(row.getChwName())).append(',')
                  .append(csv(row.getEmployeeCode())).append(',')
                  .append(csv(row.getAssignedVillage())).append(',')
                  .append(row.getActivePatients()).append(',')
                  .append(row.getHighRiskPatients()).append(',')
                  .append(row.getHomeVisits30d()).append(',')
                  .append(row.getMissedDoses7d())
                  .append("\r\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /** RFC 4180 field escaping: quote if the value contains a comma, quote, or newline. */
    private String csv(String value) {
        if (value == null) return "";
        String safe = neutralizeFormula(value);
        boolean needsQuoting = safe.contains(",") || safe.contains("\"") || safe.contains("\n");
        return needsQuoting ? "\"" + safe.replace("\"", "\"\"") + "\"" : safe;
    }

    /**
     * Prefixes values that start with =, +, -, or @ with a single quote so spreadsheet
     * apps (Excel, Sheets) display them as text instead of executing them as formulas.
     * See OWASP CSV Injection guidance.
     */
    private String neutralizeFormula(String value) {
        if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) {
            return "'" + value;
        }
        return value;
    }
}

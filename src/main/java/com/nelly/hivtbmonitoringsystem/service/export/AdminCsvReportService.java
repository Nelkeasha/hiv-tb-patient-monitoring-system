package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportRow;
import com.nelly.hivtbmonitoringsystem.service.export.support.CsvUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Renders the admin report's facility breakdown as flat CSV — the
 * system-integration counterpart to the Excel workbook, one row per
 * facility, for feeding into an external HMIS or analytics pipeline.
 */
@Service
public class AdminCsvReportService {

    private static final String[] HEADERS = {
            "facility_name", "district", "active_patients", "total_chws",
            "adherence_avg_pct", "high_risk_patients", "unresolved_alerts",
    };

    public byte[] generate(AdminReportResponse report) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", HEADERS)).append("\r\n");

        if (report.getFacilityBreakdown() != null) {
            for (FacilityReportRow row : report.getFacilityBreakdown()) {
                sb.append(CsvUtil.escape(row.getFacilityName())).append(',')
                  .append(CsvUtil.escape(row.getDistrict())).append(',')
                  .append(row.getActivePatients()).append(',')
                  .append(row.getTotalChws()).append(',')
                  .append(row.getAdherenceAvg() != null ? row.getAdherenceAvg() : "").append(',')
                  .append(row.getHighRiskPatients()).append(',')
                  .append(row.getUnresolvedAlerts())
                  .append("\r\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}

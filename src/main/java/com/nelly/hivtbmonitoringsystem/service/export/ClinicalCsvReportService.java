package com.nelly.hivtbmonitoringsystem.service.export;

import com.nelly.hivtbmonitoringsystem.dto.response.ChwPerformanceRow;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportResponse;
import com.nelly.hivtbmonitoringsystem.service.export.support.CsvUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Renders the facility (clinical) report's CHW performance breakdown as
 * flat CSV — the system-integration counterpart to the official PDF, for
 * feeding into an external HMIS or analytics pipeline.
 */
@Service
public class ClinicalCsvReportService {

    private static final String[] HEADERS = {
            "chw_name", "employee_code", "assigned_village",
            "active_patients", "visits_30d", "missed_doses_30d",
    };

    public byte[] generate(FacilityReportResponse report) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", HEADERS)).append("\r\n");

        if (report.getChwPerformance() != null) {
            for (ChwPerformanceRow row : report.getChwPerformance()) {
                sb.append(CsvUtil.escape(row.getChwName())).append(',')
                  .append(CsvUtil.escape(row.getEmployeeCode())).append(',')
                  .append(CsvUtil.escape(row.getAssignedVillage())).append(',')
                  .append(row.getActivePatients()).append(',')
                  .append(row.getVisitsLast30Days()).append(',')
                  .append(row.getMissedDosesLast30Days())
                  .append("\r\n");
            }
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}

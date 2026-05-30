package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class FacilityReportRow {
    private String facilityName;
    private String district;
    private long activePatients;
    private long totalChws;
    private BigDecimal adherenceAvg;
    private long highRiskPatients;
    private long unresolvedAlerts;
}

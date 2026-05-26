package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class SupervisorStatsResponse {
    private String facilityName;
    private String district;
    private long totalChws;
    private long activeChws;
    private long totalActivePatients;
    private long highRiskPatients;
    private long criticalAlerts;
    private long pendingChwAlerts;
    private long totalHomeVisits30d;
    private long totalMissedDoses7d;
    private BigDecimal facilityAdherenceAvg;
}

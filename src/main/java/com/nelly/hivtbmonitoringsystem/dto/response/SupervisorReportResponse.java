package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class SupervisorReportResponse {

    private String facilityName;
    private String district;
    private LocalDateTime generatedAt;

    // Workforce
    private long totalChws;
    private long activeChws;

    // Patient overview
    private long totalActivePatients;
    private long hivOnly;
    private long tbOnly;
    private long hivTbCoinfection;

    // Risk distribution (latest score per patient)
    private long riskLow;
    private long riskModerate;
    private long riskHigh;
    private long riskCritical;
    private long riskUnscored;

    // Adherence & activity
    private BigDecimal facilityAdherenceAvg;
    private long belowThresholdCount;
    private long totalHomeVisits30d;
    private long totalMissedDoses7d;

    // Alerts (unresolved)
    private long unresolvedAlerts;
    private long criticalAlerts;
    private long warningAlerts;
    private long missedDoseAlerts;
    private long earlyWarningAlerts;

    // CHW performance (last 30 days)
    private List<SupervisorChwReportRow> chwPerformance;
}

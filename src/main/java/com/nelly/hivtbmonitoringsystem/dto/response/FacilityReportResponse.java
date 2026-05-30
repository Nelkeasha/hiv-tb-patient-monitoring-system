package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class FacilityReportResponse {

    private String facilityName;
    private String district;
    private LocalDateTime generatedAt;

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

    // Adherence
    private BigDecimal facilityAdherenceAvg;
    private long belowThresholdCount;
    private long falseConfirmationFlagCount;

    // Referrals (all-time)
    private long referralTotal;
    private long referralPending;
    private long referralConfirmed;
    private long referralAttended;
    private long referralNotAttended;
    private long referralCancelled;

    // Alerts (currently unresolved)
    private long unresolvedAlerts;
    private long criticalAlerts;
    private long warningAlerts;

    // CHW performance (last 30 days)
    private List<ChwPerformanceRow> chwPerformance;
}

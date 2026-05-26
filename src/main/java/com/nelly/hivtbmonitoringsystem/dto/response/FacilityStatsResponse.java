package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class FacilityStatsResponse {
    private String facilityName;
    private String district;
    private long totalActivePatients;
    private long totalChws;
    private long activeTreatmentPlans;
    private long highRiskPatientCount;
    private long criticalAlertCount;
    private long belowThresholdCount;
    private BigDecimal facilityAdherenceAvg;
}

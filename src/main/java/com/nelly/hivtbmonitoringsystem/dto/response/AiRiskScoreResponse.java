package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class AiRiskScoreResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private RiskLevel riskLevel;
    private BigDecimal riskScore;
    private Integer suspicionScore;
    private Integer missedDoses7d;
    private Integer missedDoses14d;
    private Integer missedDoses30d;
    private Integer avgResponseTimeSeconds;
    private Integer sideEffectReports14d;
    private Integer missedVisits30d;
    private Boolean timestampAnomalyDetected;
    private Boolean pillCountDiscrepancyDetected;
    private Boolean windowViolationDetected;
    private String recommendedAction;
    private LocalDateTime calculatedAt;
}

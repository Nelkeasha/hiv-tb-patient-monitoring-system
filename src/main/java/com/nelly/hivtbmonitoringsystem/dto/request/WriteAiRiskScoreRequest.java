package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class WriteAiRiskScoreRequest {

    @NotNull
    private UUID patientId;

    @NotNull
    private RiskLevel riskLevel;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
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
}

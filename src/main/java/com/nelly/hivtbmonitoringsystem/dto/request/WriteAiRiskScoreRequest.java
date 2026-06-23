package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class WriteAiRiskScoreRequest {

    @NotNull(message = ValidationMessages.PATIENT_ID_REQUIRED)
    private UUID patientId;

    @NotNull(message = ValidationMessages.RISK_LEVEL_REQUIRED)
    private RiskLevel riskLevel;

    @NotNull(message = ValidationMessages.RISK_SCORE_REQUIRED)
    @DecimalMin(value = "0.00", message = ValidationMessages.RISK_SCORE_RANGE)
    @DecimalMax(value = "100.00", message = ValidationMessages.RISK_SCORE_RANGE)
    private BigDecimal riskScore;

    @Min(value = 0, message = "Suspicion score cannot be negative")
    private Integer suspicionScore;
    @Min(value = 0, message = "Missed doses (7 days) cannot be negative")
    private Integer missedDoses7d;
    @Min(value = 0, message = "Missed doses (14 days) cannot be negative")
    private Integer missedDoses14d;
    @Min(value = 0, message = "Missed doses (30 days) cannot be negative")
    private Integer missedDoses30d;
    @Min(value = 0, message = "Average response time cannot be negative")
    private Integer avgResponseTimeSeconds;
    @Min(value = 0, message = "Side-effect reports (14 days) cannot be negative")
    private Integer sideEffectReports14d;
    @Min(value = 0, message = "Missed visits (30 days) cannot be negative")
    private Integer missedVisits30d;
    private Boolean timestampAnomalyDetected;
    private Boolean pillCountDiscrepancyDetected;
    private Boolean windowViolationDetected;
    private String recommendedAction;
}

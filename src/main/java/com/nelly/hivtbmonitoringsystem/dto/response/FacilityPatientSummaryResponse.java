package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Builder
public class FacilityPatientSummaryResponse {
    private UUID id;
    private String patientCode;
    private String fullName;
    private DiagnosisType diagnosisType;
    private Boolean isActive;
    private String chwName;
    private RiskLevel riskLevel;
    private BigDecimal riskScore;
    private String recommendedAction;
}

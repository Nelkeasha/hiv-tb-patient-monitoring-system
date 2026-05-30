package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Builder
public class PriorityPatientResponse {
    private UUID patientId;
    private String patientName;
    private String patientCode;
    private String village;
    private BigDecimal riskScore;
    private String riskLevel;
    private String priorityGroup;
    private String recommendedAction;
}

package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HomeVisitTaskResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private String patientCode;
    private String village;
    private String diagnosisType;
    private UUID chwId;
    private String chwName;
    private String triggerType;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
}

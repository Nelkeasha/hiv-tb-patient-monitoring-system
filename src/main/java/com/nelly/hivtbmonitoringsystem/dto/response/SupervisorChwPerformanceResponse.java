package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter @Builder
public class SupervisorChwPerformanceResponse {
    private UUID id;
    private String fullName;
    private String employeeCode;
    private String assignedVillage;
    private String assignedSector;
    private long totalPatients;
    private long activePatients;
    private long homeVisits30d;
    private long missedDoses7d;
    private long highRiskPatients;
    private Boolean isActive;
}

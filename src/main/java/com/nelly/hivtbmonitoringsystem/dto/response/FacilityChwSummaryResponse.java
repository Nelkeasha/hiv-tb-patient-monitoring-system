package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter @Builder
public class FacilityChwSummaryResponse {
    private UUID id;
    private String fullName;
    private String employeeCode;
    private String assignedVillage;
    private String assignedSector;
    private long totalPatients;
    private long activePatients;
    private Boolean isActive;
}

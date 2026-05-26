package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter @Builder
public class SupervisorChwDetailResponse {
    private UUID id;
    private String fullName;
    private String employeeCode;
    private String assignedVillage;
    private String assignedSector;
    private Boolean isActive;
    private long homeVisits30d;
    private long missedDoses7d;
    private List<FacilityPatientSummaryResponse> patients;
    private List<HomeVisitResponse> recentHomeVisits;
}

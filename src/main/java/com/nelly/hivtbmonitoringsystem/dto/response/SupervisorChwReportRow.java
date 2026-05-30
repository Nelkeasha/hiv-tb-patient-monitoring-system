package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class SupervisorChwReportRow {
    private String chwName;
    private String employeeCode;
    private String assignedVillage;
    private long activePatients;
    private long highRiskPatients;
    private long homeVisits30d;
    private long missedDoses7d;
}

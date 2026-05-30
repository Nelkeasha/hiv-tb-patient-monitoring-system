package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class ChwPerformanceRow {
    private String chwName;
    private String employeeCode;
    private String assignedVillage;
    private long activePatients;
    private long visitsLast30Days;
    private long missedDosesLast30Days;
}

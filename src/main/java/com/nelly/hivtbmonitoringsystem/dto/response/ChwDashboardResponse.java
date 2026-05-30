package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class ChwDashboardResponse {
    private int totalPatients;
    private int visitTodayCount;
    private int callTodayCount;
    private int stableCount;
    private int activeAlerts;
    private String chwName;
    private String chwCode;
}

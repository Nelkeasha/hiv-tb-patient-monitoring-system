package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class SystemSettingsResponse {
    private Integer missedDoseThreshold;
    private Integer lowStockDays;
    private Integer confirmWindowMinutes;
    private Integer highRiskThreshold;
    private Integer criticalRiskThreshold;
    private LocalDateTime updatedAt;
}

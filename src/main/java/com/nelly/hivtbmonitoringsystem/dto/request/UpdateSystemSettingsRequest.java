package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateSystemSettingsRequest {

    @Min(1) @Max(10)
    private Integer missedDoseThreshold;

    @Min(1) @Max(90)
    private Integer lowStockDays;

    @Min(1) @Max(180)
    private Integer confirmWindowMinutes;

    @Min(1) @Max(100)
    private Integer highRiskThreshold;

    @Min(1) @Max(100)
    private Integer criticalRiskThreshold;
}

package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockResponse {

    private UUID id;
    private String medicationName;
    private Integer currentQuantity;
    private Integer reorderLevel;
    private String unit;
    private Integer daysRemaining;
    private Boolean resupplyRequested;
    private Boolean belowReorderLevel;
    private LocalDateTime lastRestockedAt;
    private LocalDateTime updatedAt;
}

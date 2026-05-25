package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DispenseResponse {

    private UUID dispensingEventId;
    private String medicationName;
    private Integer quantityDispensed;
    private String patientName;
    private LocalDateTime dispensedAt;
    private StockResponse updatedStock;
}

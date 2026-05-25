package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitStockRequest {

    @NotBlank(message = "Medication name is required")
    private String medicationName;

    @NotNull(message = "Initial quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer initialQuantity;

    @Min(value = 1, message = "Reorder level must be at least 1")
    private Integer reorderLevel = 14;

    private String unit = "tablets";
}

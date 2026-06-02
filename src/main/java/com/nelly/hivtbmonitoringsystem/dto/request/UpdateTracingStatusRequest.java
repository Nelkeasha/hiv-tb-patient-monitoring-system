package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateTracingStatusRequest {
    @NotBlank
    private String status; // LATE | CHW_ASSIGNED | RESOLVED | LTFU_CONFIRMED | ESCALATED
    private String notes;
}

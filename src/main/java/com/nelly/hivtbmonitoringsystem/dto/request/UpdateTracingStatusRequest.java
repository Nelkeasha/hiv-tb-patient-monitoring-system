package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateTracingStatusRequest {
    @NotBlank
    @Pattern(regexp = "LATE|CHW_ASSIGNED|LTFU_CONFIRMED|ESCALATED",
             message = "Status must be one of: LATE, CHW_ASSIGNED, LTFU_CONFIRMED, ESCALATED")
    private String status;
    private String notes;
}

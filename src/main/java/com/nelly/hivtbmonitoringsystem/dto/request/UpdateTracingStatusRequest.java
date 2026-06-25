package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateTracingStatusRequest {
    @NotBlank
    @Pattern(regexp = "LATE|IIT_ESCALATED|TREATMENT_INTERRUPTED|ESCALATED",
             message = "Status must be one of: LATE, IIT_ESCALATED, TREATMENT_INTERRUPTED, ESCALATED")
    private String status;
    private String notes;
}

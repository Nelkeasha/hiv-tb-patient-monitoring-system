package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResolveTracingTaskRequest {
    @NotBlank
    private String outcome; // PATIENT_FOUND | PATIENT_REFUSED | PATIENT_HOSPITALIZED | PROXY_AUTHORIZED | UNABLE_TO_LOCATE
    private String disengagementReason; // STIGMA | TRANSPORT_COST | SIDE_EFFECTS | etc.
    private String resolutionPlan;
    private Boolean proxyAuthorized;
    private String proxyName;
    private String notes;
}

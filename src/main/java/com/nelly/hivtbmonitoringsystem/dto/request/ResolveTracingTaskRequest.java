package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ResolveTracingTaskRequest {
    @NotBlank
    @Pattern(regexp = "PATIENT_FOUND|PATIENT_REFUSED|PATIENT_HOSPITALIZED|PROXY_AUTHORIZED|UNABLE_TO_LOCATE",
             message = "Outcome must be one of: PATIENT_FOUND, PATIENT_REFUSED, PATIENT_HOSPITALIZED, PROXY_AUTHORIZED, UNABLE_TO_LOCATE")
    private String outcome;

    @Pattern(regexp = "STIGMA|TRANSPORT_COST|SIDE_EFFECTS|FEELING_HEALTHY|WORK_RELOCATION|FAMILY_ISSUES|OTHER",
             message = "Disengagement reason must be one of: STIGMA, TRANSPORT_COST, SIDE_EFFECTS, " +
                     "FEELING_HEALTHY, WORK_RELOCATION, FAMILY_ISSUES, OTHER")
    private String disengagementReason;
    private String resolutionPlan;
    private Boolean proxyAuthorized;
    private String proxyName;
    private String notes;
}

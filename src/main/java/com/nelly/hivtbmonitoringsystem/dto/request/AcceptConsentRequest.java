package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptConsentRequest {

    /** Identifies which version of the consent text the user agreed to (e.g. "2026-06-v1"). */
    @NotBlank(message = "consentVersion is required")
    private String consentVersion;
}

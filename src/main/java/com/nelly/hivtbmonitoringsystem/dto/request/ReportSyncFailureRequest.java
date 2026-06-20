package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Reported by the mobile app when an offline-queued action (home visit,
 * dose confirmation) was rejected by the server in a way that retrying
 * cannot fix (e.g. a duplicate confirmation). Surfaces as a SYNC_FAILURE
 * alert visible to the CHW, facility provider, and supervisor — the device
 * that recorded the action already keeps its own local "needs attention"
 * copy for the CHW/patient themselves.
 */
@Getter @Setter
public class ReportSyncFailureRequest {

    @NotBlank
    @Pattern(regexp = "HOME_VISIT|DOSE_CONFIRMATION",
             message = "Action type must be HOME_VISIT or DOSE_CONFIRMATION")
    private String actionType;

    /** Required for HOME_VISIT (CHW reports about a specific patient); ignored for
     *  DOSE_CONFIRMATION, where the reporting patient is always the subject. */
    private UUID patientId;

    @NotBlank
    private String reason;
}

package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Masked view of a not-yet-accepted CHW assignment — deliberately omits the
 * patient's name and diagnosis. The CHW only sees enough to know a task
 * exists; calling PatientController#acceptAssignment unlocks the full record.
 */
@Getter @Builder
public class PendingAssignmentResponse {
    private UUID patientId;
    private String village;
    private String sector;
    private String protocol;
    private LocalDateTime assignedAt;
}

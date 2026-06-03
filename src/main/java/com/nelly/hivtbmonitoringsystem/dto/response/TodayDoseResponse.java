package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class TodayDoseResponse {
    private UUID id;
    private UUID patientId;
    private String medicationName;
    private String scheduledTime;
    private LocalDateTime windowOpenTime;
    private LocalDateTime windowCloseTime;
    private Boolean isConfirmed;
    private Boolean isMissed;
    private LocalDateTime confirmedAt;
    private String prescribedBy;
    private String facilityName;
    private String prescriptionSource;
}

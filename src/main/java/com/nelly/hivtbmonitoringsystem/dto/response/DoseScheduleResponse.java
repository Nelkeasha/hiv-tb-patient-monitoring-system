package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class DoseScheduleResponse {
    private UUID id;
    private UUID planId;
    private UUID patientId;
    private LocalTime doseTime;
    private String doseLabel;
    private ConfirmationChannel notificationMethod;
    private Integer windowDurationMinutes;
    private Boolean isActive;
    private String createdByName;
    private String prescriptionSource;
    private LocalDateTime createdAt;
}

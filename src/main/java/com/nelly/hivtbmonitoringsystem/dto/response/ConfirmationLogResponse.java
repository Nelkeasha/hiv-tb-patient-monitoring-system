package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class ConfirmationLogResponse {
    private UUID id;
    private UUID patientId;
    private UUID planId;
    private UUID scheduleId;
    private LocalDate scheduledDate;
    private LocalDateTime windowOpenTime;
    private LocalDateTime windowCloseTime;
    private LocalDateTime confirmedAt;
    private Integer responseTimeSeconds;
    private ConfirmationChannel confirmationMethod;
    private Boolean isWithinWindow;
    private Boolean isMissed;
    private Boolean aiSuspicionFlag;
    private String suspicionReason;
    private LocalDateTime createdAt;
}

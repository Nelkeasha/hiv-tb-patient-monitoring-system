package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class AlertResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private UUID chwId;
    private AlertType alertType;
    private AlertSeverity severity;
    private String title;
    private String message;
    private Boolean isRead;
    private Boolean isResolved;
    private LocalDateTime resolvedAt;
    private String resolvedByName;
    private LocalDateTime createdAt;
}

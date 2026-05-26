package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class FhirSyncLogResponse {
    private UUID id;
    private UUID chwId;
    private String chwName;
    private String syncStatus;
    private int recordsSynced;
    private int recordsFailed;
    private LocalDateTime syncStartedAt;
    private LocalDateTime syncCompletedAt;
    private String errorLog;
    private LocalDateTime createdAt;
}

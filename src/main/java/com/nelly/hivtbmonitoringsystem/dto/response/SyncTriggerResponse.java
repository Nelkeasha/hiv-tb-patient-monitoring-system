package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class SyncTriggerResponse {
    private UUID logId;
    private String syncStatus;
    private int pendingPatients;
    private int pendingHomeVisits;
    private int pendingMedicationRecords;
    private int pendingTreatmentPlans;
    private int totalQueued;
    private LocalDateTime syncStartedAt;
}

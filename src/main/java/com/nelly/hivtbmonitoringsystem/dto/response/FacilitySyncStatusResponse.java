package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class FacilitySyncStatusResponse {
    private String facilityName;
    private long pendingPatients;
    private long pendingHomeVisits;
    private long pendingMedicationRecords;
    private long pendingTreatmentPlans;
    private long total;
    private LocalDateTime lastSyncCompletedAt;
}

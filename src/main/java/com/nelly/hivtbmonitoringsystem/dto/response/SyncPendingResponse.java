package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class SyncPendingResponse {
    private int pendingPatients;
    private int pendingHomeVisits;
    private int pendingMedicationRecords;
    private int pendingTreatmentPlans;
    private int total;
}

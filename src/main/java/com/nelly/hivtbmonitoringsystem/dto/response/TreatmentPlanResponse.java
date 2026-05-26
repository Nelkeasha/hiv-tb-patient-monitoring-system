package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Builder
public class TreatmentPlanResponse {
    private UUID id;
    private UUID patientId;
    private String patientName;
    private String medicationName;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private SyncStatus syncStatus;
    private LocalDateTime createdAt;
    private List<DoseScheduleResponse> schedules;
}

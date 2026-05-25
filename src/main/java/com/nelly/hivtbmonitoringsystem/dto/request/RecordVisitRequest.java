package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecordVisitRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;

    @NotNull(message = "Adherence status is required")
    private String adherenceStatus;

    private Integer pillCountRecorded;
    private Integer pillCountExpected;
    private String symptomsReported;
    private String sideEffectsReported;
    private String psychosocialNotes;
    private LocalDateTime nextVisitDate;
}

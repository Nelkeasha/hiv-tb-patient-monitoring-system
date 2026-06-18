package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecordVisitRequest {

    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @NotNull(message = "Visit date is required")
    @Past(message = "Visit date must be in the past")
    private LocalDateTime visitDate;

    @NotNull(message = "Adherence status is required")
    @Pattern(regexp = "ADHERING|NON_ADHERING|PARTIAL",
             message = "Adherence status must be ADHERING, NON_ADHERING, or PARTIAL")
    private String adherenceStatus;

    @Min(value = 0, message = "Pill count recorded cannot be negative")
    private Integer pillCountRecorded;

    @Min(value = 0, message = "Pill count expected cannot be negative")
    private Integer pillCountExpected;

    private String symptomsReported;
    private String sideEffectsReported;
    private String psychosocialNotes;
    private LocalDateTime nextVisitDate;

    /** Optional — set by the mobile app's offline outbox so a retried queue-flush is a safe no-op. */
    private UUID clientRequestId;
}

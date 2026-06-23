package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecordVisitRequest {

    @NotNull(message = ValidationMessages.PATIENT_ID_REQUIRED)
    private UUID patientId;

    @NotNull(message = ValidationMessages.VISIT_DATE_REQUIRED)
    @Past(message = ValidationMessages.VISIT_DATE_NOT_FUTURE)
    private LocalDateTime visitDate;

    @NotNull(message = ValidationMessages.ADHERENCE_STATUS_REQUIRED)
    @Pattern(regexp = ValidationPatterns.ADHERENCE_STATUS, message = ValidationMessages.ADHERENCE_STATUS_INVALID)
    private String adherenceStatus;

    @Min(value = 0, message = ValidationMessages.PILL_COUNT_NEGATIVE)
    private Integer pillCountRecorded;

    @Min(value = 0, message = ValidationMessages.PILL_COUNT_NEGATIVE)
    private Integer pillCountExpected;

    private String symptomsReported;
    private String sideEffectsReported;
    private String psychosocialNotes;
    private LocalDateTime nextVisitDate;

    /** Optional — set by the mobile app's offline outbox so a retried queue-flush is a safe no-op. */
    private UUID clientRequestId;
}

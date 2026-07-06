package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import jakarta.validation.constraints.Max;
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

    // ── Structured symptom screen (Gap B). Free-text below is now an optional "other" note. ──
    private Boolean symptomCoughGe2w;
    private Boolean symptomFever;
    private Boolean symptomNightSweats;
    private Boolean symptomWeightLoss;
    private Boolean symptomHemoptysis;
    private Boolean sideEffectNeuropathy;
    private Boolean sideEffectJaundice;
    private Boolean sideEffectNausea;
    private Boolean sideEffectRash;
    private Boolean sideEffectDizziness;

    private String symptomsReported;
    private String sideEffectsReported;
    private String psychosocialNotes;
    private LocalDateTime nextVisitDate;

    /** CTCAE-style severity grade for any adverse drug reaction observed during the visit, 1-4. Null = none reported. */
    @Min(value = 1, message = ValidationMessages.ADVERSE_EVENT_GRADE_RANGE)
    @Max(value = 4, message = ValidationMessages.ADVERSE_EVENT_GRADE_RANGE)
    private Integer adverseEventGrade;

    /** Whether the CHW initiated a clinical referral for this visit — expected for grade 3/4 adverse events. */
    private Boolean referralInitiated;

    /** Optional — set by the mobile app's offline outbox so a retried queue-flush is a safe no-op. */
    private UUID clientRequestId;
}

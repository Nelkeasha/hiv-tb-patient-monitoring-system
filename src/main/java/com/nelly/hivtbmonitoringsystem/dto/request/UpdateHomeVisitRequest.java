package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Corrects an already-submitted home visit (e.g. fixing a typo in notes).
 * Optimistic locking: recordVersion must match the row's current value or
 * the update is rejected with 409 so a stale edit can't silently clobber
 * a newer one (e.g. two devices editing the same visit while offline).
 */
@Data
public class UpdateHomeVisitRequest {

    @NotNull(message = ValidationMessages.RECORD_VERSION_REQUIRED)
    private Integer recordVersion;

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

    @Min(value = 1, message = ValidationMessages.ADVERSE_EVENT_GRADE_RANGE)
    @Max(value = 4, message = ValidationMessages.ADVERSE_EVENT_GRADE_RANGE)
    private Integer adverseEventGrade;

    /** Whether the CHW initiated a clinical referral for this visit — expected for grade 3/4 adverse events. */
    private Boolean referralInitiated;
}

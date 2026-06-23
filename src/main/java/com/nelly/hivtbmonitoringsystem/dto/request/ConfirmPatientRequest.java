package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaNationalId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

/**
 * Route B confirmation — Clinical staff upgrades a PROVISIONAL patient to ACTIVE.
 */
@Data
public class ConfirmPatientRequest {

    @RwandaNationalId
    private String nationalPatientId; // stored as national_id

    @NotNull(message = ValidationMessages.DIAGNOSIS_TYPE_REQUIRED)
    private DiagnosisType diagnosisType;

    @PastOrPresent(message = "ART start date cannot be in the future")
    private LocalDate artStartDate;

    @PastOrPresent(message = "TB treatment start date cannot be in the future")
    private LocalDate tbTreatmentStartDate;

    private String labResultNotes;
}

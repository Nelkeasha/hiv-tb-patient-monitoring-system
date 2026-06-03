package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Route B confirmation — Clinical staff upgrades a PROVISIONAL patient to ACTIVE.
 */
@Data
public class ConfirmPatientRequest {

    private String nationalPatientId; // stored as national_id

    @NotNull
    private DiagnosisType diagnosisType;

    private LocalDate artStartDate;

    private LocalDate tbTreatmentStartDate;

    private String labResultNotes;
}

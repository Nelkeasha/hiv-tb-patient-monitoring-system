package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.constraints.DateRangeOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@DateRangeOrder(start = "startDate", end = "endDate")
public class CreateTreatmentPlanRequest {

    @NotNull(message = ValidationMessages.PATIENT_ID_REQUIRED)
    private UUID patientId;

    @NotNull(message = ValidationMessages.MEDICATION_ID_REQUIRED)
    private UUID medicationId;

    @NotBlank(message = ValidationMessages.DOSAGE_REQUIRED)
    @Size(max = 50, message = "Dosage must be at most 50 characters")
    private String dosage;

    @NotBlank(message = ValidationMessages.FREQUENCY_REQUIRED)
    @Size(max = 50, message = "Frequency must be at most 50 characters")
    private String frequency;

    @NotNull(message = ValidationMessages.START_DATE_REQUIRED)
    private LocalDate startDate;

    private LocalDate endDate;

    /** Optional — if provided, one DoseSchedule is auto-created per entry. */
    private List<LocalTime> doseTimes;
}

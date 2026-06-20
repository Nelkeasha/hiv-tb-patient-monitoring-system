package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UpdateTreatmentPlanRequest {
    @Size(max = 100, message = "Medication name must be at most 100 characters")
    private String medicationName;

    @Size(max = 50, message = "Dosage must be at most 50 characters")
    private String dosage;

    @Size(max = 50, message = "Frequency must be at most 50 characters")
    private String frequency;

    private LocalDate endDate;
    private Boolean isActive;
}

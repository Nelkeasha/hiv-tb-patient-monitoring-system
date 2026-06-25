package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
public class UpdateTreatmentPlanRequest {
    private UUID medicationId;

    @Size(max = 50, message = "Dosage must be at most 50 characters")
    private String dosage;

    @Size(max = 50, message = "Frequency must be at most 50 characters")
    private String frequency;

    private LocalDate endDate;
    private Boolean isActive;
}

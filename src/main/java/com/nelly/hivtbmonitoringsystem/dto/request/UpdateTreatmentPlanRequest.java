package com.nelly.hivtbmonitoringsystem.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UpdateTreatmentPlanRequest {
    private String medicationName;
    private String dosage;
    private String frequency;
    private LocalDate endDate;
    private Boolean isActive;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class GenerateTracingTaskRequest {
    @NotNull
    private UUID patientId;
    @NotNull
    private LocalDate missedAppointmentDate;
    @NotBlank
    private String reason; // MISSED_REFILL | MISSED_APPOINTMENT | LOST_TO_FOLLOWUP
}

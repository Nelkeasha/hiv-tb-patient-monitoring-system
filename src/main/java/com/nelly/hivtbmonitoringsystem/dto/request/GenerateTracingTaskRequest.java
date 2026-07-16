package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class GenerateTracingTaskRequest {
    @NotNull
    private UUID patientId;
    @NotNull
    @PastOrPresent(message = "Missed appointment date cannot be in the future")
    private LocalDate missedAppointmentDate;
    @NotBlank
    @Pattern(regexp = "MISSED_REFILL|MISSED_APPOINTMENT|LOST_TO_FOLLOWUP",
             message = "Reason must be one of: MISSED_REFILL, MISSED_APPOINTMENT, LOST_TO_FOLLOWUP")
    private String reason;
}

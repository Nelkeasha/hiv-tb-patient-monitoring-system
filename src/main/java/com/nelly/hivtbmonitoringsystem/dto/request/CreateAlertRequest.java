package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CreateAlertRequest {

    private UUID patientId;
    private UUID chwId;
    private UUID providerId;
    private UUID supervisorId;

    @NotNull
    private AlertType alertType;

    @NotNull
    private AlertSeverity severity;

    @NotBlank
    private String title;

    @NotBlank
    private String message;
}

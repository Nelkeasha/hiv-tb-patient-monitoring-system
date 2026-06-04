package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestEmailRequest {

    @Email
    @NotBlank
    private String recipientEmail;

    @NotBlank
    private String notificationType;

    private String patientName;
    private String patientId;
    private Integer daysSinceMissed;
    private String message;
    private String recommendedAction;
}

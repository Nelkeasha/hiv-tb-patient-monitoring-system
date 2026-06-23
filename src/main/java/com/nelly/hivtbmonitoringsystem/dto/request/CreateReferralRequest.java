package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ReferralUrgency;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateReferralRequest {

    @NotNull(message = ValidationMessages.PATIENT_ID_REQUIRED)
    private UUID patientId;

    @NotBlank(message = ValidationMessages.REFERRAL_REASON_REQUIRED)
    @Size(max = 500, message = "Referral reason must be at most 500 characters")
    private String referralReason;

    @NotNull(message = ValidationMessages.REFERRAL_URGENCY_REQUIRED)
    private ReferralUrgency urgency;

    private LocalDate referralDate;
}

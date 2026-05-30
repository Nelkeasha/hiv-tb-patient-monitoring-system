package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ReferralUrgency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateReferralRequest {

    @NotNull
    private UUID patientId;

    @NotBlank
    private String referralReason;

    @NotNull
    private ReferralUrgency urgency;

    private LocalDate referralDate;
}

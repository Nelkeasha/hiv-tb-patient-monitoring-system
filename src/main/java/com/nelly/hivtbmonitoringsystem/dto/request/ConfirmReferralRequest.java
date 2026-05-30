package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ConfirmReferralRequest {

    @NotNull
    private LocalDate appointmentDate;

    private String providerNotes;

    private ReferralStatus status;
}

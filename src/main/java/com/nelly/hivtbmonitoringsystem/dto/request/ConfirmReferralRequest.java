package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ConfirmReferralRequest {

    @NotNull(message = "Facility appointment date is required")
    private LocalDate facilityAppointmentDate;

    @Size(max = 1000, message = "Provider notes must be at most 1000 characters")
    private String providerNotes;

    private ReferralStatus status;
}

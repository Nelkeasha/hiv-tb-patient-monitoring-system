package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter @Setter
public class AddDoseScheduleRequest {

    @NotNull(message = ValidationMessages.DOSE_TIME_REQUIRED)
    private LocalTime doseTime;

    @Size(max = 50, message = "Dose label must be at most 50 characters")
    private String doseLabel;

    private ConfirmationChannel notificationMethod = ConfirmationChannel.APP;

    // The confirmation window is not client-configurable: every schedule uses the
    // system-wide confirm_window_minutes setting (45 min), whatever the dose time.

    @Size(max = 100, message = "Prescription source must be at most 100 characters")
    private String prescriptionSource;
}

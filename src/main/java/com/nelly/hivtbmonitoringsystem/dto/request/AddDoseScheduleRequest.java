package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter @Setter
public class AddDoseScheduleRequest {

    @NotNull(message = "Dose time is required (e.g. 08:00:00)")
    private LocalTime doseTime;

    private String doseLabel;

    private ConfirmationChannel notificationMethod = ConfirmationChannel.APP;

    @jakarta.validation.constraints.Min(value = 1, message = "Window duration must be at least 1 minute")
    private Integer windowDurationMinutes = 45;

    private String prescriptionSource;
}

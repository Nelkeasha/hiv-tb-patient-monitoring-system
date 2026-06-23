package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class SubmitConfirmationRequest {

    @NotNull(message = ValidationMessages.SCHEDULE_ID_REQUIRED)
    private UUID scheduleId;

    private ConfirmationChannel confirmationMethod = ConfirmationChannel.APP;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateProviderRequest {

    @NotBlank(message = ValidationMessages.FULL_NAME_REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @NotBlank(message = ValidationMessages.PHONE_REQUIRED)
    @RwandaPhone
    private String phoneNumber;

    @NotNull(message = ValidationMessages.FACILITY_REQUIRED)
    private UUID facilityId;

    @Size(max = 100, message = "Specialization must be at most 100 characters")
    private String specialization;

    @Size(max = 50, message = "License number must be at most 50 characters")
    private String licenseNumber;
}

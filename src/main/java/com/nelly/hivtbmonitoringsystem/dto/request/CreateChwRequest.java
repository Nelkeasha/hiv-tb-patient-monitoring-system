package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateChwRequest {

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

    @NotBlank(message = ValidationMessages.VILLAGE_REQUIRED)
    @Size(max = 100, message = ValidationMessages.VILLAGE_TOO_LONG)
    private String assignedVillage;

    @NotBlank(message = ValidationMessages.SECTOR_REQUIRED)
    @Size(max = 100, message = ValidationMessages.SECTOR_TOO_LONG)
    private String assignedSector;

    @NotBlank(message = ValidationMessages.EMPLOYEE_CODE_REQUIRED)
    @Pattern(regexp = ValidationPatterns.EMPLOYEE_CODE, message = ValidationMessages.EMPLOYEE_CODE_INVALID)
    private String employeeCode;
}

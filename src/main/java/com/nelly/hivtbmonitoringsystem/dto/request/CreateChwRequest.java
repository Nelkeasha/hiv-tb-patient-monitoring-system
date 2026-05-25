package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateChwRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotNull
    private UUID facilityId;

    @NotBlank
    private String assignedVillage;

    @NotBlank
    private String assignedSector;

    @NotBlank
    private String employeeCode;
}

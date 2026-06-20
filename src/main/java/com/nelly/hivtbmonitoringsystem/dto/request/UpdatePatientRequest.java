package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePatientRequest {

    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phoneNumber;

    private Boolean hasSmartphone;

    @Size(max = 255, message = "Household location must be at most 255 characters")
    private String householdLocation;

    @Size(max = 100, message = "Village must be at most 100 characters")
    private String village;

    @Size(max = 100, message = "Sector must be at most 100 characters")
    private String sector;

    @Size(max = 100, message = "District must be at most 100 characters")
    private String district;
}

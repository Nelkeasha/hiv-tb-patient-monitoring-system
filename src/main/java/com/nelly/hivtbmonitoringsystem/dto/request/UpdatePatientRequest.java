package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePatientRequest {

    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @RwandaPhone
    private String phoneNumber;

    private Boolean hasSmartphone;

    @Size(max = 255, message = ValidationMessages.HOUSEHOLD_LOCATION_TOO_LONG)
    private String householdLocation;

    @Size(max = 100, message = ValidationMessages.VILLAGE_TOO_LONG)
    private String village;

    @Size(max = 100, message = ValidationMessages.SECTOR_TOO_LONG)
    private String sector;

    @Size(max = 100, message = ValidationMessages.DISTRICT_TOO_LONG)
    private String district;
}

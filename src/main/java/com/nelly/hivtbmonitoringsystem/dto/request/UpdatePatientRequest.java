package com.nelly.hivtbmonitoringsystem.dto.request;

import lombok.Data;

@Data
public class UpdatePatientRequest {

    private String fullName;
    private String phoneNumber;
    private Boolean hasSmartphone;
    private String householdLocation;
    private String village;
    private String sector;
    private String district;
}

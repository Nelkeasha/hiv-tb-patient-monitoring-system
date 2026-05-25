package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PatientResponse {

    private UUID id;
    private String patientCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String sex;
    private String nationalId;
    private String phoneNumber;
    private Boolean hasSmartphone;
    private String diagnosisType;
    private LocalDate artStartDate;
    private LocalDate tbTreatmentStartDate;
    private String householdLocation;
    private String village;
    private String sector;
    private String district;
    private UUID chwId;
    private String chwName;
    private UUID facilityId;
    private String facilityName;
    private String syncStatus;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

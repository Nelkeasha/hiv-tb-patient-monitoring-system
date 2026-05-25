package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollPatientRequest {

    @NotBlank
    private String patientCode;

    @NotBlank
    private String fullName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    private String sex;

    private String nationalId;

    private String phoneNumber;

    private Boolean hasSmartphone = false;

    @NotNull
    private DiagnosisType diagnosisType;

    private LocalDate artStartDate;

    private LocalDate tbTreatmentStartDate;

    private String householdLocation;

    private String village;

    private String sector;

    private String district;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollPatientRequest {

    private String patientCode;

    @NotBlank
    private String fullName;

    private LocalDate dateOfBirth;

    @JsonAlias("gender")
    @Pattern(regexp = "MALE|FEMALE|OTHER|UNKNOWN", message = "Sex must be MALE, FEMALE, OTHER, or UNKNOWN")
    private String sex;

    private String nationalId;

    private String phoneNumber;

    private Boolean hasSmartphone = false;

    /** Preferred: single diagnosisType enum value. */
    private DiagnosisType diagnosisType;

    /** Alternative: Flutter sends hivStatus + tbStatus separately. */
    private String hivStatus;
    private String tbStatus;

    private LocalDate artStartDate;

    private LocalDate tbTreatmentStartDate;

    private String householdLocation;

    private String village;

    private String sector;

    private String district;
}

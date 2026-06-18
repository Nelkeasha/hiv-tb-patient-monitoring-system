package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Route A — Clinical staff facility registration request.
 * Creates an ACTIVE patient record with confirmed diagnosis.
 */
@Data
public class RegisterPatientRequest {

    @NotBlank
    private String fullName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    @jakarta.validation.constraints.Pattern(regexp = "MALE|FEMALE|OTHER",
             message = "Sex must be MALE, FEMALE, or OTHER")
    private String sex;

    private String nationalId;
    private String phoneNumber;
    private Boolean hasSmartphone = false;

    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String householdLocation;

    @NotNull
    private DiagnosisType diagnosisType;

    private LocalDate artStartDate;
    private LocalDate tbTreatmentStartDate;

    /** The CHW who will be assigned to monitor this patient. */
    @NotNull
    private UUID assignedChwId;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollPatientRequest {

    private String patientCode;

    @NotBlank
    private String fullName;

    private LocalDate dateOfBirth;

    /** Accepts "sex" field name. */
    private String sex;

    /** Alias — Flutter sends "gender", mapped to sex in service. */
    private String gender;

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

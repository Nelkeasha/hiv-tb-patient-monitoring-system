package com.nelly.hivtbmonitoringsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaNationalId;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EnrollPatientRequest {

    private String patientCode;

    @NotBlank(message = ValidationMessages.FULL_NAME_REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @PastOrPresent(message = ValidationMessages.DATE_OF_BIRTH_NOT_FUTURE)
    private LocalDate dateOfBirth;

    @JsonAlias("gender")
    @Pattern(regexp = ValidationPatterns.SEX, message = ValidationMessages.SEX_INVALID)
    private String sex;

    @RwandaNationalId
    private String nationalId;

    @RwandaPhone
    private String phoneNumber;

    private Boolean hasSmartphone = false;

    /** Preferred: single diagnosisType enum value. */
    private DiagnosisType diagnosisType;

    /** Alternative: Flutter sends hivStatus + tbStatus separately. */
    private String hivStatus;
    private String tbStatus;

    @PastOrPresent(message = "ART start date cannot be in the future")
    private LocalDate artStartDate;

    @PastOrPresent(message = "TB treatment start date cannot be in the future")
    private LocalDate tbTreatmentStartDate;

    @Size(max = 255, message = ValidationMessages.HOUSEHOLD_LOCATION_TOO_LONG)
    private String householdLocation;

    @Size(max = 100, message = ValidationMessages.VILLAGE_TOO_LONG)
    private String village;

    @Size(max = 100, message = ValidationMessages.SECTOR_TOO_LONG)
    private String sector;

    @Size(max = 100, message = ValidationMessages.DISTRICT_TOO_LONG)
    private String district;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Route B — CHW provisional screening request.
 * Creates a PROVISIONAL patient record with a generated referral ID.
 * Clinical staff must confirm before treatment can begin.
 */
@Data
public class ScreenPatientRequest {

    @NotBlank(message = ValidationMessages.FULL_NAME_REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @PastOrPresent(message = ValidationMessages.DATE_OF_BIRTH_NOT_FUTURE)
    private LocalDate dateOfBirth;

    @JsonAlias("gender")
    @Pattern(regexp = ValidationPatterns.SEX, message = ValidationMessages.SEX_INVALID)
    private String sex;

    @RwandaPhone
    private String phoneNumber;
    private Boolean hasSmartphone = false;

    private String province;
    @Size(max = 100, message = ValidationMessages.DISTRICT_TOO_LONG)
    private String district;
    @Size(max = 100, message = ValidationMessages.SECTOR_TOO_LONG)
    private String sector;
    private String cell;
    @Size(max = 100, message = ValidationMessages.VILLAGE_TOO_LONG)
    private String village;
    @Size(max = 255, message = ValidationMessages.HOUSEHOLD_LOCATION_TOO_LONG)
    private String householdLocation;

    /** TB | HIV | HIV_TB_COINFECTION */
    @Pattern(regexp = ValidationPatterns.DIAGNOSIS_SUSPECTED_CONDITION, message = ValidationMessages.SUSPECTED_CONDITION_INVALID)
    private String suspectedCondition;

    private List<String> symptoms;

    private String screeningNotes;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String fullName;

    private LocalDate dateOfBirth;

    private String sex;
    private String gender; // alias

    private String phoneNumber;
    private Boolean hasSmartphone = false;

    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    private String householdLocation;

    /** TB | HIV | HIV_TB_COINFECTION */
    private String suspectedCondition;

    private List<String> symptoms;

    private String screeningNotes;
}

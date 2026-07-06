package com.nelly.hivtbmonitoringsystem.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.AssertTrue;
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

    /** Geohash computed on-device from a one-time GPS read — the server never receives raw lat/long. */
    @Pattern(regexp = ValidationPatterns.GEOHASH, message = ValidationMessages.GEOHASH_INVALID)
    private String locationGeohash;

    /** TB | HIV | HIV_TB_COINFECTION */
    @Pattern(regexp = ValidationPatterns.DIAGNOSIS_SUSPECTED_CONDITION, message = ValidationMessages.SUSPECTED_CONDITION_INVALID)
    private String suspectedCondition;

    private List<String> symptoms;

    // ── RBC structured TB symptom screen (V32). presumptiveTb is recomputed
    //    server-side from these five — the client value is never trusted. ──
    private Boolean tbSymptomCough;
    private Boolean tbSymptomFever;
    private Boolean tbSymptomNightSweats;
    private Boolean tbSymptomWeightLoss;
    private Boolean tbSymptomChestPain;
    private Boolean presumptiveTb;

    // ── Community HIV testing-eligibility risk screen (V32). hivTestingReferral
    //    is likewise recomputed server-side from the five risk answers. ──
    private Boolean hivRiskNeverTested;
    private Boolean hivRiskPartnerPositive;
    private Boolean hivRiskUnprotectedSex;
    private Boolean hivRiskStiTreatment;
    private Boolean hivRiskRecurrentIllness;
    private Boolean hivTestingReferral;

    @Size(max = 200, message = ValidationMessages.MANUAL_REFERRAL_REASON_TOO_LONG)
    private String manualReferralReason;

    private String screeningNotes;

    /**
     * The patient (or guardian) must have given documented consent to data
     * collection — captured in person, in this screen, before the record can
     * be created. Rwanda Law No. 058/2021; HIV/TB status is
     * special-category sensitive data.
     */
    @AssertTrue(message = ValidationMessages.CONSENT_REQUIRED)
    private boolean consentGiven;

    @NotBlank(message = ValidationMessages.CONSENT_VERSION_REQUIRED)
    private String consentVersion;
}

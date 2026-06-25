package com.nelly.hivtbmonitoringsystem.dto.request;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaNationalId;
import com.nelly.hivtbmonitoringsystem.validation.constraints.RwandaPhone;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Route A — Clinical staff facility registration request.
 * Creates an ACTIVE patient record with confirmed diagnosis.
 */
@Data
public class RegisterPatientRequest {

    @NotBlank(message = ValidationMessages.FULL_NAME_REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @NotNull(message = ValidationMessages.DATE_OF_BIRTH_REQUIRED)
    @PastOrPresent(message = ValidationMessages.DATE_OF_BIRTH_NOT_FUTURE)
    private LocalDate dateOfBirth;

    @NotBlank(message = ValidationMessages.SEX_INVALID)
    @Pattern(regexp = ValidationPatterns.SEX_DEFINITIVE, message = ValidationMessages.SEX_INVALID)
    private String sex;

    @RwandaNationalId
    private String nationalId;

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

    @NotNull(message = ValidationMessages.DIAGNOSIS_TYPE_REQUIRED)
    private DiagnosisType diagnosisType;

    @PastOrPresent(message = "ART start date cannot be in the future")
    private LocalDate artStartDate;
    @PastOrPresent(message = "TB treatment start date cannot be in the future")
    private LocalDate tbTreatmentStartDate;

    /**
     * The CHW who will be assigned to monitor this patient. Optional — if omitted,
     * the system auto-matches a CHW by village/sector (see PatientService#matchChwByLocation).
     */
    private UUID assignedChwId;

    /**
     * The patient (or guardian) must have given documented consent to data
     * collection — captured in person at registration — before the record
     * can be created. Rwanda Law No. 058/2021; HIV/TB status is
     * special-category sensitive data.
     */
    @AssertTrue(message = ValidationMessages.CONSENT_REQUIRED)
    private boolean consentGiven;

    @NotBlank(message = ValidationMessages.CONSENT_VERSION_REQUIRED)
    private String consentVersion;
}

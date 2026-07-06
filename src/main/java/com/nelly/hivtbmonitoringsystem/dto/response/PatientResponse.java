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
    private String province;
    private String cell;
    private String locationGeohash;
    private UUID chwId;
    private String chwName;
    private UUID facilityId;
    private String facilityName;
    private String syncStatus;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String loginEmail;
    private String temporaryPassword;

    /** Date of the next scheduled CHW home visit (from latest home visit record). */
    private LocalDate nextCHWVisitDate;

    // ── Registration route fields (V9) ────────────────────────────────────────

    /** FACILITY (Route A) or CHW_SCREENING (Route B). */
    private String registrationRoute;

    /** ACTIVE or PROVISIONAL. */
    private String registrationStatus;

    /** REF-2026-KIM-0042 — generated for Route B provisional records. */
    private String referralId;

    /** What the CHW suspected at screening time. */
    private String suspectedCondition;

    /** CHW's clinical notes from the field screening. */
    private String screeningNotes;

    /** Clinical staff notes after lab confirmation. */
    private String labResultNotes;

    /** When the record was promoted from PROVISIONAL to ACTIVE. */
    private LocalDateTime confirmedAt;

    // ── RBC structured TB symptom screen (V32) ────────────────────────────────
    private Boolean tbSymptomCough;
    private Boolean tbSymptomFever;
    private Boolean tbSymptomNightSweats;
    private Boolean tbSymptomWeightLoss;
    private Boolean tbSymptomChestPain;
    private Boolean presumptiveTb;

    // ── Community HIV testing-risk screen (V32) — null for supervisors/admins ──
    private Boolean hivRiskNeverTested;
    private Boolean hivRiskPartnerPositive;
    private Boolean hivRiskUnprotectedSex;
    private Boolean hivRiskStiTreatment;
    private Boolean hivRiskRecurrentIllness;
    private Boolean hivTestingReferral;
    private String manualReferralReason;
}

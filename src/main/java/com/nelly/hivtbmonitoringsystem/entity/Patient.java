package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "patient_code", unique = true, nullable = false, length = 20)
    private String patientCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 10)
    private String sex;

    @Column(name = "national_id", unique = true, length = 16)
    private String nationalId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "has_smartphone")
    private Boolean hasSmartphone = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "diagnosis_type", nullable = false, columnDefinition = "diagnosis_type")
    private DiagnosisType diagnosisType;

    @Column(name = "art_start_date")
    private LocalDate artStartDate;

    @Column(name = "tb_treatment_start_date")
    private LocalDate tbTreatmentStartDate;

    @Column(name = "household_location", length = 255)
    private String householdLocation;

    @Column(length = 100)
    private String village;

    @Column(length = 100)
    private String sector;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String province;

    @Column(length = 100)
    private String cell;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SystemUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id", nullable = false)
    private Chw chw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "registration_route", length = 20)
    private String registrationRoute = "FACILITY";

    /**
     * Acceptance gate for the CHW/patient relationship itself — distinct from
     * registrationStatus (which tracks the patient record's clinical lifecycle).
     * Route A (facility self-presented) patients start PENDING so the assigned
     * CHW must explicitly accept before the full record (name, diagnosis) is
     * visible to them; Route B (CHW screening) patients are ACCEPTED immediately
     * since the screening CHW already knows who they are.
     */
    @Builder.Default
    @Column(name = "chw_assignment_status", length = 20)
    private String chwAssignmentStatus = "ACCEPTED";

    @Column(name = "chw_assigned_at")
    private LocalDateTime chwAssignedAt;

    @Column(name = "chw_accepted_at")
    private LocalDateTime chwAcceptedAt;

    @Column(name = "chw_assignment_reminder_sent_at")
    private LocalDateTime chwAssignmentReminderSentAt;

    @Column(name = "chw_assignment_escalated_at")
    private LocalDateTime chwAssignmentEscalatedAt;

    @Column(name = "registration_status", length = 20)
    private String registrationStatus = "PROVISIONAL";

    /** Geohash-encoded location (precision 7, ~150m cell) — never raw lat/long. */
    @Column(name = "location_geohash", length = 12)
    private String locationGeohash;

    @Column(name = "referral_id", unique = true, length = 30)
    private String referralId;

    /**
     * Patient's own documented consent to data collection (Rwanda Law No.
     * 058/2021) — captured by the registering CHW/clinical staff at the
     * point of registration, distinct from {@code SystemUser.consentGiven}
     * which covers an app-account holder agreeing to app terms.
     */
    @Column(name = "consent_given", nullable = false)
    private Boolean consentGiven = false;

    @Column(name = "consent_timestamp")
    private LocalDateTime consentTimestamp;

    @Column(name = "consent_version", length = 20)
    private String consentVersion;

    @Column(name = "screened_by_chw_id")
    private UUID screenedByChwId;

    @Column(name = "screened_at")
    private LocalDateTime screenedAt;

    @Column(name = "confirmed_by")
    private UUID confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "suspected_condition", length = 50)
    private String suspectedCondition;

    @Column(name = "screening_symptoms", columnDefinition = "TEXT")
    private String screeningSymptoms;

    // ── RBC structured TB symptom screen (V32) ────────────────────────────────
    @Builder.Default @Column(name = "tb_symptom_cough", nullable = false)
    private Boolean tbSymptomCough = false;
    @Builder.Default @Column(name = "tb_symptom_fever", nullable = false)
    private Boolean tbSymptomFever = false;
    @Builder.Default @Column(name = "tb_symptom_night_sweats", nullable = false)
    private Boolean tbSymptomNightSweats = false;
    @Builder.Default @Column(name = "tb_symptom_weight_loss", nullable = false)
    private Boolean tbSymptomWeightLoss = false;
    @Builder.Default @Column(name = "tb_symptom_chest_pain", nullable = false)
    private Boolean tbSymptomChestPain = false;
    @Builder.Default @Column(name = "presumptive_tb", nullable = false)
    private Boolean presumptiveTb = false;

    // ── Community HIV testing-eligibility risk screen (V32, sensitive) ─────────
    @Builder.Default @Column(name = "hiv_risk_never_tested", nullable = false)
    private Boolean hivRiskNeverTested = false;
    @Builder.Default @Column(name = "hiv_risk_partner_positive", nullable = false)
    private Boolean hivRiskPartnerPositive = false;
    @Builder.Default @Column(name = "hiv_risk_unprotected_sex", nullable = false)
    private Boolean hivRiskUnprotectedSex = false;
    @Builder.Default @Column(name = "hiv_risk_sti_treatment", nullable = false)
    private Boolean hivRiskStiTreatment = false;
    @Builder.Default @Column(name = "hiv_risk_recurrent_illness", nullable = false)
    private Boolean hivRiskRecurrentIllness = false;
    @Builder.Default @Column(name = "hiv_testing_referral", nullable = false)
    private Boolean hivTestingReferral = false;
    @Column(name = "manual_referral_reason", length = 200)
    private String manualReferralReason;

    @Column(name = "screening_notes", columnDefinition = "TEXT")
    private String screeningNotes;

    @Column(name = "lab_result_notes", columnDefinition = "TEXT")
    private String labResultNotes;

    @Column(name = "fhir_patient_id", unique = true, length = 100)
    private String fhirPatientId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sync_status", columnDefinition = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

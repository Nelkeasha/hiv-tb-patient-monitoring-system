package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "home_visits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HomeVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id", nullable = false)
    private Chw chw;

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "adherence_status", nullable = false, length = 20)
    private String adherenceStatus;

    @Column(name = "pill_count_recorded")
    private Integer pillCountRecorded;

    @Column(name = "pill_count_expected")
    private Integer pillCountExpected;

    @Column(name = "pill_count_discrepancy")
    private Boolean pillCountDiscrepancy = false;

    @Column(name = "symptoms_reported", columnDefinition = "TEXT")
    private String symptomsReported;

    @Column(name = "side_effects_reported", columnDefinition = "TEXT")
    private String sideEffectsReported;

    // ── Structured symptom screen (Gap B) — WHO four-symptom TB screen + hemoptysis ──
    @Builder.Default @Column(name = "symptom_cough_ge2w", nullable = false)
    private Boolean symptomCoughGe2w = false;
    @Builder.Default @Column(name = "symptom_fever", nullable = false)
    private Boolean symptomFever = false;
    @Builder.Default @Column(name = "symptom_night_sweats", nullable = false)
    private Boolean symptomNightSweats = false;
    @Builder.Default @Column(name = "symptom_weight_loss", nullable = false)
    private Boolean symptomWeightLoss = false;
    @Builder.Default @Column(name = "symptom_hemoptysis", nullable = false)
    private Boolean symptomHemoptysis = false;

    // ── Standardized ART / TLD side-effect checklist (Gap B) ──
    @Builder.Default @Column(name = "side_effect_neuropathy", nullable = false)
    private Boolean sideEffectNeuropathy = false;
    @Builder.Default @Column(name = "side_effect_jaundice", nullable = false)
    private Boolean sideEffectJaundice = false;
    @Builder.Default @Column(name = "side_effect_nausea", nullable = false)
    private Boolean sideEffectNausea = false;
    @Builder.Default @Column(name = "side_effect_rash", nullable = false)
    private Boolean sideEffectRash = false;
    @Builder.Default @Column(name = "side_effect_dizziness", nullable = false)
    private Boolean sideEffectDizziness = false;

    /** Server-derived: any WHO cardinal TB symptom present → screen positive → refer for sputum testing. */
    @Builder.Default @Column(name = "presumptive_tb", nullable = false)
    private Boolean presumptiveTb = false;

    @Column(name = "psychosocial_notes", columnDefinition = "TEXT")
    private String psychosocialNotes;

    @Column(name = "next_visit_date")
    private LocalDateTime nextVisitDate;

    @Column(name = "visit_status", length = 20, nullable = false)
    private String visitStatus = "ATTENDED_TO";

    @Column(name = "fhir_observation_id", length = 100)
    private String fhirObservationId;

    /** Set by the mobile app's offline outbox so a retried queue-flush can't create a duplicate visit. */
    @Column(name = "client_request_id")
    private UUID clientRequestId;

    /** CTCAE-style severity grade for any adverse drug reaction observed during the visit, 1-4. Null = none reported. */
    @Column(name = "adverse_event_grade")
    private Integer adverseEventGrade;

    @Builder.Default
    @Column(name = "referral_initiated", nullable = false)
    private Boolean referralInitiated = false;

    /** Optimistic-locking counter — incremented on every update; mismatched value on write returns 409. */
    @Builder.Default
    @Column(name = "record_version", nullable = false)
    private Integer recordVersion = 0;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sync_status", columnDefinition = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

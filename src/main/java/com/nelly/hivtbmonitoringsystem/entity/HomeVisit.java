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

    @Column(name = "psychosocial_notes", columnDefinition = "TEXT")
    private String psychosocialNotes;

    @Column(name = "next_visit_date")
    private LocalDateTime nextVisitDate;

    @Column(name = "fhir_observation_id", length = 100)
    private String fhirObservationId;

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

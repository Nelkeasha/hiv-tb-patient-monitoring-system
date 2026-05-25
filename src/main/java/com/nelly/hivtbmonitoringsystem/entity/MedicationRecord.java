package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedicationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id", nullable = false)
    private Chw chw;

    @Column(name = "medication_name", nullable = false, length = 100)
    private String medicationName;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "doses_scheduled", nullable = false)
    private Integer dosesScheduled;

    @Column(name = "doses_taken", nullable = false)
    private Integer dosesTaken;

    @Column(name = "adherence_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal adherencePct;

    @Column(name = "below_threshold")
    private Boolean belowThreshold = false;

    @Column(name = "fhir_resource_id", length = 100)
    private String fhirResourceId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sync_status", columnDefinition = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
}

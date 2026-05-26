package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @JoinColumn(name = "plan_id")
    private TreatmentPlan plan;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "doses_scheduled", nullable = false)
    private Integer dosesScheduled;

    @Column(name = "doses_confirmed", nullable = false)
    private Integer dosesConfirmed;

    @Column(name = "doses_verified", nullable = false)
    private Integer dosesVerified;

    @Column(name = "adherence_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal adherencePct;

    @Column(name = "below_threshold")
    private Boolean belowThreshold = false;

    @Column(name = "false_confirmation_flag")
    private Boolean falseConfirmationFlag = false;

    @Column(name = "fhir_statement_id", length = 100)
    private String fhirStatementId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sync_status", columnDefinition = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

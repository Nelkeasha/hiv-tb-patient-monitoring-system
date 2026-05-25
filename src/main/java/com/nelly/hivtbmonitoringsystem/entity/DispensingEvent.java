package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dispensing_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DispensingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockRecord stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id", nullable = false)
    private Chw chw;

    @Column(name = "medication_name", nullable = false, length = 100)
    private String medicationName;

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    @Column(name = "dispensed_at")
    private LocalDateTime dispensedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private HomeVisit visit;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sync_status", columnDefinition = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        dispensedAt = LocalDateTime.now();
    }
}

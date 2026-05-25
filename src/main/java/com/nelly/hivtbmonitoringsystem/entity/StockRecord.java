package com.nelly.hivtbmonitoringsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id", nullable = false)
    private Chw chw;

    @Column(name = "medication_name", nullable = false, length = 100)
    private String medicationName;

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity = 0;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 14;

    @Column(length = 20)
    private String unit = "tablets";

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;

    @Column(name = "days_remaining")
    private Integer daysRemaining;

    @Column(name = "resupply_requested")
    private Boolean resupplyRequested = false;

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

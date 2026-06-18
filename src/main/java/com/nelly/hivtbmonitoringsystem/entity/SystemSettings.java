package com.nelly.hivtbmonitoringsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "missed_dose_threshold", nullable = false)
    private Integer missedDoseThreshold;

    @Column(name = "low_stock_days", nullable = false)
    private Integer lowStockDays;

    @Column(name = "confirm_window_minutes", nullable = false)
    private Integer confirmWindowMinutes;

    @Column(name = "high_risk_threshold", nullable = false)
    private Integer highRiskThreshold;

    @Column(name = "critical_risk_threshold", nullable = false)
    private Integer criticalRiskThreshold;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_risk_scores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiRiskScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "risk_level", nullable = false, columnDefinition = "risk_level")
    private RiskLevel riskLevel;

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "suspicion_score")
    private Integer suspicionScore = 0;

    @Column(name = "missed_doses_7d")
    private Integer missedDoses7d = 0;

    @Column(name = "missed_doses_14d")
    private Integer missedDoses14d = 0;

    @Column(name = "missed_doses_30d")
    private Integer missedDoses30d = 0;

    @Column(name = "avg_response_time_seconds")
    private Integer avgResponseTimeSeconds;

    @Column(name = "side_effect_reports_14d")
    private Integer sideEffectReports14d = 0;

    @Column(name = "missed_visits_30d")
    private Integer missedVisits30d = 0;

    @Column(name = "timestamp_anomaly_detected")
    private Boolean timestampAnomalyDetected = false;

    @Column(name = "pill_count_discrepancy_detected")
    private Boolean pillCountDiscrepancyDetected = false;

    @Column(name = "window_violation_detected")
    private Boolean windowViolationDetected = false;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
}

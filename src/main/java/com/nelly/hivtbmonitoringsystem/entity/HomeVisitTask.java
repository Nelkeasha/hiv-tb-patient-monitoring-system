package com.nelly.hivtbmonitoringsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A triggered in-person home-visit task for a CHW. Created only when a real
 * clinical trigger fires (missed doses, side effect, IIT escalation, high AI
 * risk, a scheduled periodic review, or a new patient's initial assessment) —
 * never as a routine daily visit. Carries the reason so the CHW knows why.
 */
@Entity
@Table(name = "home_visit_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HomeVisitTask {

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

    /** MISSED_DOSES | SIDE_EFFECT | IIT_ESCALATED | HIGH_RISK | PERIODIC_REVIEW | INITIAL_ASSESSMENT */
    @Column(name = "trigger_type", nullable = false, length = 30)
    private String triggerType;

    @Column(name = "reason", length = 255)
    private String reason;

    /** OPEN | COMPLETED */
    @Builder.Default
    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** The home visit that closed this task, if any. */
    @Column(name = "completed_visit_id")
    private UUID completedVisitId;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

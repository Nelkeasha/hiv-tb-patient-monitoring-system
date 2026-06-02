package com.nelly.hivtbmonitoringsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Records each LTFU tracing assignment per the Rwanda national LTFU protocol.
 * Thesis Table 19 — tracks the full lifecycle from LATE → CHW_ASSIGNED →
 * LTFU_CONFIRMED, including disengagement barriers and re-engagement plans.
 */
@Entity
@Table(name = "tracing_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TracingTask {

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

    /** Calendar date the patient missed their scheduled facility appointment. */
    @Column(name = "missed_appointment_date", nullable = false)
    private LocalDate missedAppointmentDate;

    /** Days elapsed since missedAppointmentDate — recalculated daily by LtfuScheduler. */
    @Column(name = "days_since_missed", nullable = false)
    private Integer daysSinceMissed = 0;

    /** Why the task was triggered: MISSED_REFILL | MISSED_APPOINTMENT | LOST_TO_FOLLOWUP */
    @Column(name = "reason", nullable = false, length = 30)
    private String reason;

    /** Current lifecycle stage: LATE | CHW_ASSIGNED | RESOLVED | LTFU_CONFIRMED | ESCALATED */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "LATE";

    /** Set when daysSinceMissed crosses 30 per Rwanda national LTFU definition. */
    @Column(name = "ltfu_confirmed_at")
    private LocalDateTime ltfuConfirmedAt;

    /** Result of CHW tracing visit: PATIENT_FOUND | PATIENT_REFUSED |
     *  PATIENT_HOSPITALIZED | PROXY_AUTHORIZED | UNABLE_TO_LOCATE */
    @Column(name = "outcome", length = 30)
    private String outcome;

    /** Barrier identified by CHW during tracing visit. */
    @Column(name = "disengagement_reason", length = 30)
    private String disengagementReason;

    /** Re-engagement plan agreed between CHW and patient/family. */
    @Column(name = "resolution_plan", columnDefinition = "TEXT")
    private String resolutionPlan;

    /** Whether patient authorized a proxy to collect medication under Rwanda MOH protocol. */
    @Column(name = "proxy_authorized", nullable = false)
    private Boolean proxyAuthorized = false;

    @Column(name = "proxy_name", length = 100)
    private String proxyName;

    /** CHW counseling observations from the tracing visit. */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /** Supervisor notified when task reaches LTFU_CONFIRMED. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_to")
    private SystemUser escalatedTo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

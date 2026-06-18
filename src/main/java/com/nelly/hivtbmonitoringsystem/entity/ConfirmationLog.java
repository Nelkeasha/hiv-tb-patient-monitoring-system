package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "confirmation_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfirmationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private TreatmentPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private DoseSchedule schedule;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "window_open_time", nullable = false)
    private LocalDateTime windowOpenTime;

    @Column(name = "window_close_time", nullable = false)
    private LocalDateTime windowCloseTime;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "confirmation_method", nullable = false, columnDefinition = "confirmation_channel")
    private ConfirmationChannel confirmationMethod;

    @Column(name = "raw_sms_response", length = 20)
    private String rawSmsResponse;

    @Column(name = "is_within_window")
    private Boolean isWithinWindow = false;

    @Column(name = "is_missed")
    private Boolean isMissed = false;

    @Column(name = "ai_suspicion_flag")
    private Boolean aiSuspicionFlag = false;

    @Column(name = "suspicion_reason", length = 500)
    private String suspicionReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

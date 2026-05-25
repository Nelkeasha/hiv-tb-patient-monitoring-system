package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "scheduled_dose_time", nullable = false)
    private LocalDateTime scheduledDoseTime;

    @Column(name = "confirmation_time")
    private LocalDateTime confirmationTime;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "confirmation_channel")
    private ConfirmationChannel channel;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "confirmation_status")
    private ConfirmationStatus status;

    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds;

    @Column(name = "window_open_time", nullable = false)
    private LocalDateTime windowOpenTime;

    @Column(name = "window_close_time", nullable = false)
    private LocalDateTime windowCloseTime;

    @Column(name = "is_within_window")
    private Boolean isWithinWindow = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

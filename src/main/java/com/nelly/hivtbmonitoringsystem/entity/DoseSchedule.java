package com.nelly.hivtbmonitoringsystem.entity;

import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dose_schedules")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoseSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private TreatmentPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "dose_time", nullable = false)
    private LocalTime doseTime;

    @Column(name = "dose_label", length = 50)
    private String doseLabel;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "notification_method", columnDefinition = "confirmation_channel")
    private ConfirmationChannel notificationMethod = ConfirmationChannel.APP;

    @Column(name = "window_duration_minutes")
    private Integer windowDurationMinutes = 45;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private SystemUser createdBy;

    @Column(name = "prescription_source", columnDefinition = "TEXT")
    private String prescriptionSource;

    /**
     * Last calendar day a dose reminder was sent for this schedule. Lets the
     * reminder schedulers fire anywhere within the dose window (robust to a
     * missed tick) while still sending only once per day.
     */
    @Column(name = "last_reminder_date")
    private LocalDate lastReminderDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

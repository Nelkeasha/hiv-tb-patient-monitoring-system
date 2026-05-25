package com.nelly.hivtbmonitoringsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fhir_sync_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FhirSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chw_id")
    private Chw chw;

    @Column(name = "sync_started_at", nullable = false)
    private LocalDateTime syncStartedAt;

    @Column(name = "sync_completed_at")
    private LocalDateTime syncCompletedAt;

    @Column(name = "records_synced")
    private Integer recordsSynced = 0;

    @Column(name = "records_failed")
    private Integer recordsFailed = 0;

    @Column(name = "sync_status", nullable = false, length = 20)
    private String syncStatus;

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

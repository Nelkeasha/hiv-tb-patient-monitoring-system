package com.nelly.hivtbmonitoringsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private SystemUser user;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "target_table", length = 50)
    private String targetTable;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String details;

    /** SHA-256 of this entry's fields + previousHash — tamper-evident chain (V28). Null for entries written before this column existed. */
    @Column(name = "entry_hash", length = 64)
    private String entryHash;

    /** entryHash of the chain's previous entry at write time. Null = chain genesis (first entry after V28, or pre-V28 row). */
    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        // AuditLogService sets createdAt explicitly before computing the entry's
        // hash, so the persisted value must match what was hashed — only
        // default it here if nothing set it.
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

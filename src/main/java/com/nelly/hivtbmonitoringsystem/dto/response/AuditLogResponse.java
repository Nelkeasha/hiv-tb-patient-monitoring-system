package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.entity.AuditLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private String userEmail;
    private String action;
    private String targetTable;
    private UUID targetId;
    private String ipAddress;
    private String details;
    private LocalDateTime timestamp;
    private String entryHash;
    private String previousHash;

    public static AuditLogResponse from(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userEmail(log.getUser() != null ? log.getUser().getEmail() : "system")
                .action(log.getAction())
                .targetTable(log.getTargetTable())
                .targetId(log.getTargetId())
                .ipAddress(log.getIpAddress())
                .details(log.getDetails())
                .timestamp(log.getCreatedAt())
                .entryHash(log.getEntryHash())
                .previousHash(log.getPreviousHash())
                .build();
    }
}

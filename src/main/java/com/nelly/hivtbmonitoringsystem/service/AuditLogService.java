package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.AuditLogResponse;
import com.nelly.hivtbmonitoringsystem.entity.AuditLog;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.repository.AuditLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final SystemUserRepository userRepository;

    /**
     * Write an audit entry. Resolves the current authenticated user automatically.
     * Safe to call from any service — logs a warning and continues if user lookup fails.
     */
    public void log(String action, String targetTable, UUID targetId) {
        log(action, targetTable, targetId, null, null);
    }

    public void log(String action, String targetTable, UUID targetId, String details, String ipAddress) {
        try {
            SystemUser actor = resolveCurrentUser();
            AuditLog entry = AuditLog.builder()
                    .user(actor)
                    .action(action)
                    .targetTable(targetTable)
                    .targetId(targetId)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.warn("Audit log write failed for action {}: {}", action, e.getMessage());
        }
    }

    /** Returns audit entries filtered by optional action and/or userId. */
    public List<AuditLogResponse> getAll(String action) {
        return getAll(action, null);
    }

    public List<AuditLogResponse> getAll(String action, String userIdStr) {
        UUID userId = null;
        if (userIdStr != null && !userIdStr.isBlank()) {
            try { userId = UUID.fromString(userIdStr); } catch (IllegalArgumentException ignored) {}
        }

        List<AuditLog> logs;
        if (userId != null && action != null && !action.isBlank()) {
            final String act = action;
            logs = auditLogRepository.findByUserId(userId).stream()
                    .filter(l -> act.equals(l.getAction()))
                    .collect(Collectors.toList());
        } else if (userId != null) {
            logs = auditLogRepository.findByUserId(userId);
        } else if (action != null && !action.isBlank()) {
            logs = auditLogRepository.findByAction(action);
        } else {
            logs = auditLogRepository.findAll(
                    PageRequest.of(0, 200, Sort.by(Sort.Direction.DESC, "createdAt"))
            ).getContent();
        }

        return logs.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(AuditLogResponse::from)
                .collect(Collectors.toList());
    }

    private SystemUser resolveCurrentUser() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}

package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.AuditLogResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.AuditChainVerificationResponse;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
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
     * Guards the read-tail/compute-hash/save sequence so two concurrent writes
     * on this instance can't both link onto the same previous hash and fork
     * the chain. Only protects a single app instance — a horizontally scaled
     * deployment would need a DB-level lock instead, but this system runs as
     * one instance (see deployment docs).
     */
    private final Object hashChainLock = new Object();

    /**
     * Write an audit entry. Resolves the current authenticated user automatically.
     * Safe to call from any service — logs a warning and continues if user lookup fails.
     *
     * @Transactional is declared on this overload too, not just the one it
     * delegates to below — Spring's @Transactional proxy only intercepts calls
     * that come in through the bean's public interface. Since this method
     * calls the other log() overload via plain `this.log(...)` (a same-class
     * self-invocation), that inner call bypasses the proxy entirely and would
     * run with whatever transaction (if any) is already active, ignoring its
     * own REQUIRES_NEW. Most real callers use THIS overload, so the annotation
     * has to live here to actually take effect.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String targetTable, UUID targetId) {
        log(action, targetTable, targetId, null, null);
    }

    /**
     * REQUIRES_NEW is deliberate, not incidental: most callers invoke this from
     * within their own @Transactional method, where Spring's default REQUIRED
     * propagation would otherwise make this save() join (not commit with) the
     * caller's transaction. That left a window where the synchronized block
     * released hashChainLock as soon as this method returned, but the row it
     * just wrote wasn't actually durable/visible to another thread until the
     * caller's outer transaction committed later — long after the lock was
     * free. Two concurrent callers could then both read the same (still
     * uncommitted) chain tail and fork the hash chain. Committing in its own
     * transaction, inside the lock, closes that window. The accepted trade-off
     * — an audit row can persist even if the caller's own transaction later
     * rolls back — matches how audit trails are commonly built: "an action was
     * attempted" is itself worth recording independently of whether the rest
     * of that request ultimately succeeded.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String targetTable, UUID targetId, String details, String ipAddress) {
        try {
            SystemUser actor = resolveCurrentUser();
            // Truncate to microseconds — Postgres' timestamp column round-trips at
            // microsecond precision, so a nanosecond-precision value here would
            // hash differently than what verifyChain reads back later.
            LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

            synchronized (hashChainLock) {
                List<AuditLog> tail = auditLogRepository.findChainTail(PageRequest.of(0, 1));
                String previousHash = tail.isEmpty() ? null : tail.get(0).getEntryHash();

                AuditLog entry = AuditLog.builder()
                        .user(actor)
                        .action(action)
                        .targetTable(targetTable)
                        .targetId(targetId)
                        .details(details)
                        .ipAddress(ipAddress)
                        .createdAt(createdAt)
                        .previousHash(previousHash)
                        .build();
                entry.setEntryHash(computeHash(entry, actor, previousHash));
                auditLogRepository.save(entry);
            }
        } catch (Exception e) {
            log.warn("Audit log write failed for action {}: {}", action, e.getMessage());
        }
    }

    /** SHA-256 over this entry's own fields plus the previous entry's hash. */
    private String computeHash(AuditLog entry, SystemUser actor, String previousHash) {
        String canonical = String.join("|",
                String.valueOf(entry.getAction()),
                String.valueOf(entry.getTargetTable()),
                String.valueOf(entry.getTargetId()),
                actor != null ? String.valueOf(actor.getId()) : "",
                String.valueOf(entry.getDetails()),
                String.valueOf(entry.getCreatedAt()),
                previousHash != null ? previousHash : "");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /**
     * Walks the chain in write order and recomputes each entry's hash to confirm
     * it matches what's stored and correctly links to the previous entry. Any
     * mismatch means a chained row was altered or deleted after the fact.
     */
    @Transactional(readOnly = true)
    public AuditChainVerificationResponse verifyChain() {
        List<AuditLog> chain = auditLogRepository.findByEntryHashIsNotNullOrderByCreatedAtAscIdAsc();
        String expectedPrevious = null;

        for (AuditLog entry : chain) {
            if (!java.util.Objects.equals(entry.getPreviousHash(), expectedPrevious)) {
                return AuditChainVerificationResponse.broken(entry.getId(),
                        "previous_hash does not match the prior entry's hash", chain.size());
            }
            String recomputed = computeHash(entry, entry.getUser(), entry.getPreviousHash());
            if (!recomputed.equals(entry.getEntryHash())) {
                return AuditChainVerificationResponse.broken(entry.getId(),
                        "stored hash does not match the entry's own fields — row was altered", chain.size());
            }
            expectedPrevious = entry.getEntryHash();
        }

        return AuditChainVerificationResponse.intact(chain.size());
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

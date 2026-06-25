package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.AuditChainVerificationResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.AuditLogResponse;
import com.nelly.hivtbmonitoringsystem.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-log")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAuditLog(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(auditLogService.getAll(action, userId));
    }

    /** Recomputes every chained entry's hash and confirms it hasn't been tampered with since it was written. */
    @GetMapping("/verify-chain")
    public ResponseEntity<AuditChainVerificationResponse> verifyChain() {
        return ResponseEntity.ok(auditLogService.verifyChain());
    }
}

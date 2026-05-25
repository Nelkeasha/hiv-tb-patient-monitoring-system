package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.CreateChwRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateProviderRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateSupervisorRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.StaffResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.UserSummaryResponse;
import com.nelly.hivtbmonitoringsystem.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/chw")
    public ResponseEntity<StaffResponse> createChw(@Valid @RequestBody CreateChwRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createChw(request));
    }

    @PostMapping("/provider")
    public ResponseEntity<StaffResponse> createProvider(@Valid @RequestBody CreateProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createProvider(request));
    }

    @PostMapping("/supervisor")
    public ResponseEntity<StaffResponse> createSupervisor(@Valid @RequestBody CreateSupervisorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createSupervisor(request));
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<UserSummaryResponse> toggleStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(userManagementService.toggleUserStatus(id));
    }

    @PutMapping("/{id}/reset-password")
    public ResponseEntity<StaffResponse> resetPassword(@PathVariable UUID id) {
        return ResponseEntity.ok(userManagementService.resetPassword(id));
    }
}

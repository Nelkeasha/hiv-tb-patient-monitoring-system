package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.UpdateSystemSettingsRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.SystemSettingsResponse;
import com.nelly.hivtbmonitoringsystem.service.SystemSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
public class SystemSettingsController {

    private final SystemSettingsService systemSettingsService;

    @GetMapping
    public ResponseEntity<SystemSettingsResponse> get() {
        return ResponseEntity.ok(systemSettingsService.get());
    }

    @PutMapping
    public ResponseEntity<SystemSettingsResponse> update(@Valid @RequestBody UpdateSystemSettingsRequest request) {
        return ResponseEntity.ok(systemSettingsService.update(request));
    }
}

package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.service.MedicationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/medication-records")
@RequiredArgsConstructor
public class MedicationRecordAdminController {

    private final MedicationRecordService medicationRecordService;

    /**
     * One-time backfill: derives medication_records from existing
     * confirmation_logs/home_visits history. Run this once after deploying
     * MedicationRecordService so adherence stats reflect pre-existing data
     * instead of staying at 0% until new confirmations happen.
     */
    @PostMapping("/backfill")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> backfill() {
        int processed = medicationRecordService.backfillAll();
        return ResponseEntity.ok(Map.of("recordsProcessed", processed));
    }
}

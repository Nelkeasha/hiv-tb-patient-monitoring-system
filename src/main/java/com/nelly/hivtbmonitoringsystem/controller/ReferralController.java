package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.ConfirmReferralRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateReferralRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RecordAttendanceRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.ReferralResponse;
import com.nelly.hivtbmonitoringsystem.service.ReferralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    // ── CHW endpoints ─────────────────────────────────────────────────────────

    @PostMapping("/api/chw/referrals")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ReferralResponse> createReferral(
            @Valid @RequestBody CreateReferralRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referralService.createReferral(request));
    }

    @GetMapping("/api/chw/referrals")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ReferralResponse>> getChwReferrals() {
        return ResponseEntity.ok(referralService.getChwReferrals());
    }

    @GetMapping("/api/chw/referrals/patient/{patientId}")
    @PreAuthorize("hasAnyRole('CHW', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ReferralResponse>> getChwPatientReferrals(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(referralService.getChwPatientReferrals(patientId));
    }

    // ── Clinical / Facility Provider endpoints ────────────────────────────────

    @GetMapping("/api/clinical/referrals")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ReferralResponse>> getFacilityReferrals() {
        return ResponseEntity.ok(referralService.getFacilityReferrals());
    }

    @GetMapping("/api/clinical/referrals/pending")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<ReferralResponse>> getFacilityPendingReferrals() {
        return ResponseEntity.ok(referralService.getFacilityPendingReferrals());
    }

    @PutMapping("/api/clinical/referrals/{referralId}/confirm")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ReferralResponse> confirmReferral(
            @PathVariable UUID referralId,
            @Valid @RequestBody ConfirmReferralRequest request) {
        return ResponseEntity.ok(referralService.confirmReferral(referralId, request));
    }

    @PutMapping("/api/clinical/referrals/{referralId}/attendance")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ReferralResponse> recordAttendance(
            @PathVariable UUID referralId,
            @Valid @RequestBody RecordAttendanceRequest request) {
        return ResponseEntity.ok(referralService.recordAttendance(referralId, request));
    }

    @PutMapping("/api/clinical/referrals/{referralId}/cancel")
    @PreAuthorize("hasAnyRole('FACILITY_PROVIDER', 'CLINICAL_STAFF', 'ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ReferralResponse> cancelReferral(@PathVariable UUID referralId) {
        return ResponseEntity.ok(referralService.cancelReferral(referralId));
    }
}

package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.ConfirmReferralRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.CreateReferralRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RecordAttendanceRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.ReferralResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import com.nelly.hivtbmonitoringsystem.validation.BusinessRuleException;
import com.nelly.hivtbmonitoringsystem.validation.StatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final FacilityProviderRepository facilityProviderRepository;
    private final SystemUserRepository systemUserRepository;
    private final StatusTransitionValidator statusTransitionValidator;

    // ── CHW ──────────────────────────────────────────────────────────────────

    @Transactional
    public ReferralResponse createReferral(CreateReferralRequest req) {
        Chw chw = resolveChw();
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }

        LocalDate referralDate = req.getReferralDate() != null ? req.getReferralDate() : LocalDate.now();

        Referral referral = Referral.builder()
                .patient(patient)
                .referredByChw(chw)
                .referralDate(referralDate)
                .referralReason(req.getReferralReason())
                .urgency(req.getUrgency())
                .status(ReferralStatus.PENDING)
                .build();

        return toResponse(referralRepository.save(referral));
    }

    public List<ReferralResponse> getChwReferrals() {
        Chw chw = resolveChw();
        return referralRepository.findByReferredByChwIdOrderByCreatedAtDesc(chw.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<ReferralResponse> getChwPatientReferrals(UUID patientId) {
        Chw chw = resolveChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }
        return referralRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── Clinical / Facility Provider ─────────────────────────────────────────

    public List<ReferralResponse> getFacilityReferrals() {
        FacilityProvider provider = resolveProvider();
        return referralRepository
                .findByPatientFacilityIdOrderByCreatedAtDesc(provider.getFacility().getId())
                .stream().map(this::toResponse).toList();
    }

    public List<ReferralResponse> getFacilityPendingReferrals() {
        FacilityProvider provider = resolveProvider();
        return referralRepository
                .findByPatientFacilityIdAndStatusOrderByCreatedAtDesc(
                        provider.getFacility().getId(), ReferralStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReferralResponse confirmReferral(UUID referralId, ConfirmReferralRequest req) {
        FacilityProvider provider = resolveProvider();
        Referral referral = findReferralForFacility(referralId, provider);

        statusTransitionValidator.requireCurrentStatus("Referral", referral.getStatus(),
                Set.of(ReferralStatus.PENDING), "confirm or modify it");

        ReferralStatus newStatus = req.getStatus() != null ? req.getStatus() : ReferralStatus.CONFIRMED;
        referral.setStatus(newStatus);
        referral.setFacilityAppointmentDate(req.getFacilityAppointmentDate());
        referral.setProviderNotes(req.getProviderNotes());
        referral.setConfirmedByProvider(provider);

        return toResponse(referralRepository.save(referral));
    }

    @Transactional
    public ReferralResponse recordAttendance(UUID referralId, RecordAttendanceRequest req) {
        FacilityProvider provider = resolveProvider();
        Referral referral = findReferralForFacility(referralId, provider);

        statusTransitionValidator.requireCurrentStatus("Referral", referral.getStatus(),
                Set.of(ReferralStatus.CONFIRMED, ReferralStatus.MODIFIED), "record attendance");

        if (req.getStatus() != ReferralStatus.ATTENDED && req.getStatus() != ReferralStatus.NOT_ATTENDED) {
            throw new BusinessRuleException("status", "Status must be ATTENDED or NOT_ATTENDED", HttpStatus.BAD_REQUEST);
        }

        referral.setStatus(req.getStatus());
        referral.setAttendanceNotes(req.getAttendanceNotes());

        return toResponse(referralRepository.save(referral));
    }

    @Transactional
    public ReferralResponse cancelReferral(UUID referralId) {
        FacilityProvider provider = resolveProvider();
        Referral referral = findReferralForFacility(referralId, provider);

        statusTransitionValidator.requireNotTerminal("Referral", referral.getStatus(),
                Set.of(ReferralStatus.ATTENDED, ReferralStatus.NOT_ATTENDED, ReferralStatus.CANCELLED));

        referral.setStatus(ReferralStatus.CANCELLED);
        return toResponse(referralRepository.save(referral));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Referral findReferralForFacility(UUID referralId, FacilityProvider provider) {
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral not found"));
        if (!referral.getPatient().getFacility().getId().equals(provider.getFacility().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Referral does not belong to your facility");
        }
        return referral;
    }

    private Chw resolveChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
    }

    private FacilityProvider resolveProvider() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return facilityProviderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Provider profile not found"));
    }

    private ReferralResponse toResponse(Referral r) {
        return ReferralResponse.builder()
                .id(r.getId())
                .patientId(r.getPatient().getId())
                .patientName(r.getPatient().getFullName())
                .patientCode(r.getPatient().getPatientCode())
                .chwId(r.getReferredByChw().getId())
                .chwName(r.getReferredByChw().getUser().getFullName())
                .providerId(r.getConfirmedByProvider() != null ? r.getConfirmedByProvider().getId() : null)
                .providerName(r.getConfirmedByProvider() != null
                        ? r.getConfirmedByProvider().getUser().getFullName() : null)
                .referralDate(r.getReferralDate())
                .referralReason(r.getReferralReason())
                .urgency(r.getUrgency())
                .status(r.getStatus())
                .facilityAppointmentDate(r.getFacilityAppointmentDate())
                .providerNotes(r.getProviderNotes())
                .attendanceNotes(r.getAttendanceNotes())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}

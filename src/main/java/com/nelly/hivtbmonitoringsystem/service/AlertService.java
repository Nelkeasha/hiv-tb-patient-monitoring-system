package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.CreateAlertRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AlertResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final FacilityProviderRepository facilityProviderRepository;
    private final SystemUserRepository systemUserRepository;

    // ── CHW ──────────────────────────────────────────────────────────────────

    public List<AlertResponse> getChwAlerts() {
        Chw chw = resolveChw();
        return alertRepository.findByChwIdAndIsResolvedFalseOrderByCreatedAtDesc(chw.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<AlertResponse> getChwPatientAlerts(UUID patientId) {
        Chw chw = resolveChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }
        return alertRepository.findByPatientIdAndIsResolvedFalseOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── Clinical / facility provider ─────────────────────────────────────────

    public List<AlertResponse> getClinicalAlerts() {
        return alertRepository.findBySeverityInAndIsResolvedFalseOrderByCreatedAtDesc(
                        List.of(AlertSeverity.CRITICAL, AlertSeverity.WARNING))
                .stream().map(this::toResponse).toList();
    }

    public List<AlertResponse> getClinicalPatientAlerts(UUID patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        return alertRepository.findByPatientIdAndIsResolvedFalseOrderByCreatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── State mutations ───────────────────────────────────────────────────────

    @Transactional
    public AlertResponse markRead(UUID alertId) {
        Alert alert = findAndAuthorizeWrite(alertId);
        alert.setIsRead(true);
        return toResponse(alertRepository.save(alert));
    }

    @Transactional
    public AlertResponse markResolved(UUID alertId) {
        Alert alert = findAndAuthorizeWrite(alertId);
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        return toResponse(alertRepository.save(alert));
    }

    // ── Internal — called by AI microservice (SYSTEM_ADMIN) ──────────────────

    @Transactional
    public AlertResponse createAlert(CreateAlertRequest req) {
        Alert.AlertBuilder builder = Alert.builder()
                .alertType(req.getAlertType())
                .severity(req.getSeverity())
                .title(req.getTitle())
                .message(req.getMessage())
                .isRead(false)
                .isResolved(false);

        if (req.getPatientId() != null) {
            builder.patient(patientRepository.findById(req.getPatientId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found")));
        }
        if (req.getChwId() != null) {
            builder.chw(chwRepository.findById(req.getChwId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHW not found")));
        }
        if (req.getProviderId() != null) {
            builder.provider(facilityProviderRepository.findById(req.getProviderId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found")));
        }

        return toResponse(alertRepository.save(builder.build()));
    }

    // ── Called internally by MissedDoseScheduler ─────────────────────────────

    @Transactional
    public void createMissedDoseAlert(Patient patient, TreatmentPlan plan) {
        Alert alert = Alert.builder()
                .patient(patient)
                .chw(patient.getChw())
                .alertType(AlertType.MISSED_DOSE)
                .severity(AlertSeverity.WARNING)
                .title("Missed Dose — " + patient.getFullName())
                .message(String.format("Patient %s missed their %s dose scheduled for %s.",
                        patient.getFullName(),
                        plan.getMedicationName(),
                        LocalDate.now()))
                .isRead(false)
                .isResolved(false)
                .build();
        alertRepository.save(alert);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Alert findAndAuthorizeWrite(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));

        SystemUser caller = resolveCurrentUser();
        if (caller.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (alert.getChw() == null || !alert.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Alert is not assigned to you");
            }
        }
        return alert;
    }

    private Chw resolveChw() {
        return chwRepository.findByUserId(resolveCurrentUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private AlertResponse toResponse(Alert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .patientName(a.getPatient() != null ? a.getPatient().getFullName() : null)
                .chwId(a.getChw() != null ? a.getChw().getId() : null)
                .alertType(a.getAlertType())
                .severity(a.getSeverity())
                .title(a.getTitle())
                .message(a.getMessage())
                .isRead(a.getIsRead())
                .isResolved(a.getIsResolved())
                .resolvedAt(a.getResolvedAt())
                .createdAt(a.getCreatedAt())
                .build();
    }
}

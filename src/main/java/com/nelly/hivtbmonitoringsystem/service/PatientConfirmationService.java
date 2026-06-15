package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.SubmitConfirmationRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.ConfirmationLogResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationChannel;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientConfirmationService {

    private final ConfirmationLogRepository confirmationLogRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final PatientRepository patientRepository;
    private final SystemUserRepository systemUserRepository;
    private final ChwRepository chwRepository;

    @Transactional
    public ConfirmationLogResponse submitConfirmation(SubmitConfirmationRequest req) {
        Patient patient = resolvePatient();

        DoseSchedule schedule = doseScheduleRepository.findById(req.getScheduleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        if (!schedule.getPatient().getId().equals(patient.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Schedule does not belong to you");
        }
        if (!schedule.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dose schedule is not active");
        }

        LocalDate today = LocalDate.now();

        java.util.Optional<ConfirmationLog> existingLog =
                confirmationLogRepository.findByScheduleIdAndScheduledDate(schedule.getId(), today);

        existingLog.filter(existing -> existing.getConfirmedAt() != null)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Dose already confirmed today");
                });

        LocalDateTime windowOpen  = today.atTime(schedule.getDoseTime());
        LocalDateTime windowClose = windowOpen.plusMinutes(schedule.getWindowDurationMinutes());
        LocalDateTime confirmedAt = LocalDateTime.now();

        boolean isWithinWindow = !confirmedAt.isBefore(windowOpen) && !confirmedAt.isAfter(windowClose);
        int responseTimeSec = (int) Duration.between(windowOpen, confirmedAt).getSeconds();

        boolean suspicious = responseTimeSec >= 0 && responseTimeSec < 30;
        String suspicionReason = suspicious ? "Response time under 30 seconds" : null;

        ConfirmationChannel method = req.getConfirmationMethod() != null
                ? req.getConfirmationMethod()
                : ConfirmationChannel.APP;

        ConfirmationLog log = existingLog.orElseGet(() -> ConfirmationLog.builder()
                .patient(patient)
                .plan(schedule.getPlan())
                .schedule(schedule)
                .scheduledDate(today)
                .build());

        log.setWindowOpenTime(windowOpen);
        log.setWindowCloseTime(windowClose);
        log.setConfirmedAt(confirmedAt);
        log.setResponseTimeSeconds(responseTimeSec);
        log.setConfirmationMethod(method);
        log.setIsWithinWindow(isWithinWindow);
        log.setIsMissed(false);
        log.setAiSuspicionFlag(suspicious);
        log.setSuspicionReason(suspicionReason);

        return toResponse(confirmationLogRepository.save(log));
    }

    public List<ConfirmationLogResponse> getMyHistory() {
        Patient patient = resolvePatient();
        return confirmationLogRepository.findByPatientId(patient.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<ConfirmationLogResponse> getPatientHistory(UUID patientId) {
        SystemUser caller = resolveCurrentUser();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (caller.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (!patient.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
            }
        }

        return confirmationLogRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).toList();
    }

    public List<ConfirmationLogResponse> getPatientMissedDoses(UUID patientId) {
        SystemUser caller = resolveCurrentUser();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (caller.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (!patient.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
            }
        }

        return confirmationLogRepository.findByPatientIdAndIsMissedTrue(patientId)
                .stream().map(this::toResponse).toList();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Patient resolvePatient() {
        SystemUser user = resolveCurrentUser();
        return patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient profile not found"));
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private ConfirmationLogResponse toResponse(ConfirmationLog c) {
        String medName = (c.getSchedule() != null && c.getSchedule().getDoseLabel() != null)
                ? c.getSchedule().getDoseLabel() : "Medication";
        return ConfirmationLogResponse.builder()
                .id(c.getId())
                .patientId(c.getPatient().getId())
                .planId(c.getPlan() != null ? c.getPlan().getId() : null)
                .scheduleId(c.getSchedule() != null ? c.getSchedule().getId() : null)
                .medicationName(medName)
                .scheduledDate(c.getScheduledDate())
                .windowOpenTime(c.getWindowOpenTime())
                .windowCloseTime(c.getWindowCloseTime())
                .confirmedAt(c.getConfirmedAt())
                .responseTimeSeconds(c.getResponseTimeSeconds())
                .confirmationMethod(c.getConfirmationMethod())
                .isWithinWindow(c.getIsWithinWindow())
                .isMissed(c.getIsMissed())
                .aiSuspicionFlag(c.getAiSuspicionFlag())
                .suspicionReason(c.getSuspicionReason())
                .createdAt(c.getCreatedAt())
                .build();
    }
}

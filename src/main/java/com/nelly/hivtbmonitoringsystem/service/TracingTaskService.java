package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.GenerateTracingTaskRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ResolveTracingTaskRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdateTracingStatusRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.TracingTaskResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import com.nelly.hivtbmonitoringsystem.validation.StatusTransitionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TracingTaskService {

    private final TracingTaskRepository tracingTaskRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final SystemUserRepository userRepository;
    private final AlertService alertService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final AiRiskScoreService aiRiskScoreService;
    private final StatusTransitionValidator statusTransitionValidator;

    // ── Generate (system or admin creates) ───────────────────────────────────

    @Transactional
    public TracingTaskResponse generateTracingTask(GenerateTracingTaskRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        // Prevent duplicates for the same appointment date
        tracingTaskRepository.findOpenTaskByPatientAndDate(patient.getId(), req.getMissedAppointmentDate())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Open tracing task already exists for this patient and date");
                });

        Chw chw = patient.getChw();
        if (chw == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patient has no assigned CHW");
        }

        long daysSince = ChronoUnit.DAYS.between(req.getMissedAppointmentDate(), LocalDate.now());

        TracingTask task = TracingTask.builder()
                .patient(patient)
                .chw(chw)
                .missedAppointmentDate(req.getMissedAppointmentDate())
                .daysSinceMissed((int) Math.max(0, daysSince))
                .reason(req.getReason())
                .status("LATE")
                .proxyAuthorized(false)
                .build();

        task = tracingTaskRepository.save(task);
        auditLogService.log("GENERATE_TRACING_TASK", "tracing_tasks", task.getId());

        // Create LTFU_TRACING alert for the CHW
        alertService.createLtfuTracingAlert(patient, chw, task, AlertSeverity.WARNING);

        log.info("Tracing task generated: patient={} chw={} date={}",
                patient.getId(), chw.getId(), req.getMissedAppointmentDate());

        return TracingTaskResponse.from(task);
    }

    // ── Update status (CHW progresses the task) ───────────────────────────────

    @Transactional
    public TracingTaskResponse updateTracingStatus(UUID taskId, UpdateTracingStatusRequest req) {
        TracingTask task = findTask(taskId);
        validateStatusTransition(task.getStatus(), req.getStatus());

        task.setStatus(req.getStatus());
        if (req.getNotes() != null) task.setNotes(req.getNotes());

        if ("LTFU_CONFIRMED".equals(req.getStatus()) && task.getLtfuConfirmedAt() == null) {
            task.setLtfuConfirmedAt(LocalDateTime.now());
            alertService.createLtfuConfirmedAlert(task.getPatient(), task.getChw(), task);
        }

        task = tracingTaskRepository.save(task);
        auditLogService.log("UPDATE_TRACING_STATUS", "tracing_tasks", task.getId());
        return TracingTaskResponse.from(task);
    }

    // ── Resolve (CHW records outcome after tracing visit) ────────────────────

    @Transactional
    public TracingTaskResponse resolveTracingTask(UUID taskId, ResolveTracingTaskRequest req) {
        TracingTask task = findTask(taskId);

        task.setStatus("RESOLVED");
        task.setOutcome(req.getOutcome());
        task.setDisengagementReason(req.getDisengagementReason());
        task.setResolutionPlan(req.getResolutionPlan());
        task.setProxyAuthorized(Boolean.TRUE.equals(req.getProxyAuthorized()));
        task.setProxyName(req.getProxyName());
        task.setNotes(req.getNotes());
        task.setResolvedAt(LocalDateTime.now());

        task = tracingTaskRepository.save(task);
        auditLogService.log("RESOLVE_TRACING_TASK", "tracing_tasks", task.getId());

        // Notify facility provider(s) and update the patient's AI risk score
        notificationService.notifyTracingResolved(task.getPatient(), task.getChw(), task);
        aiRiskScoreService.recalculateAfterTracingResolution(task.getPatient(), task.getOutcome());

        log.info("Tracing task resolved: id={} outcome={}", taskId, req.getOutcome());
        return TracingTaskResponse.from(task);
    }

    // ── Escalate to supervisor ────────────────────────────────────────────────

    @Transactional
    public TracingTaskResponse escalateToSupervisor(UUID taskId) {
        TracingTask task = findTask(taskId);

        // Find supervisor for the facility
        SystemUser supervisor = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("SUPERVISOR") && Boolean.TRUE.equals(u.getIsActive()))
                .findFirst()
                .orElse(null);

        task.setStatus("ESCALATED");
        task.setEscalatedTo(supervisor);

        if (task.getLtfuConfirmedAt() == null) {
            task.setLtfuConfirmedAt(LocalDateTime.now());
        }

        task = tracingTaskRepository.save(task);
        alertService.createLtfuConfirmedAlert(task.getPatient(), task.getChw(), task);
        auditLogService.log("ESCALATE_TRACING_TASK", "tracing_tasks", task.getId());
        log.info("Tracing task escalated: id={} supervisor={}", taskId,
                supervisor != null ? supervisor.getEmail() : "none");
        return TracingTaskResponse.from(task);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<TracingTaskResponse> getDailyTracingTasks(UUID chwId) {
        return tracingTaskRepository.findActiveByCHWOrderByUrgency(chwId)
                .stream().map(TracingTaskResponse::from).toList();
    }

    public List<TracingTaskResponse> getDailyTracingTasksForCurrentChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Chw chw = chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHW not found"));
        return getDailyTracingTasks(chw.getId());
    }

    public List<TracingTaskResponse> getPatientTracingHistory(UUID patientId) {
        return tracingTaskRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(TracingTaskResponse::from).toList();
    }

    public List<TracingTaskResponse> getEscalatedTasks() {
        return tracingTaskRepository.findByStatusOrderByLtfuConfirmedAtDesc("ESCALATED")
                .stream().map(TracingTaskResponse::from).toList();
    }

    public List<TracingTaskResponse> getLtfuConfirmedTasks() {
        return tracingTaskRepository.findByStatusOrderByLtfuConfirmedAtDesc("LTFU_CONFIRMED")
                .stream().map(TracingTaskResponse::from).toList();
    }

    // ─── Internal ─────────────────────────────────────────────────────────────

    private TracingTask findTask(UUID id) {
        return tracingTaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracing task not found"));
    }

    private void validateStatusTransition(String current, String next) {
        statusTransitionValidator.requireNotTerminal("Tracing task", current, Set.of("RESOLVED"));
    }
}

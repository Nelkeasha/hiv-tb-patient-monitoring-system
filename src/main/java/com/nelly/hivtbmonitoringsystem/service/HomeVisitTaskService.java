package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.HomeVisitTaskResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.FacilityProvider;
import com.nelly.hivtbmonitoringsystem.entity.HomeVisitTask;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.FacilityProviderRepository;
import com.nelly.hivtbmonitoringsystem.repository.HomeVisitTaskRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates and serves triggered home-visit tasks. {@link #createTask} is
 * idempotent — at most one OPEN task per patient per trigger — so repeated
 * firing of the same trigger (e.g. the missed-dose scheduler each night) does
 * not spam the CHW's list.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HomeVisitTaskService {

    private final HomeVisitTaskRepository taskRepository;
    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;
    private final FacilityProviderRepository facilityProviderRepository;

    /** Trigger constants. */
    public static final String MISSED_DOSES = "MISSED_DOSES";
    public static final String SIDE_EFFECT = "SIDE_EFFECT";
    public static final String IIT_ESCALATED = "IIT_ESCALATED";
    public static final String HIGH_RISK = "HIGH_RISK";
    public static final String PERIODIC_REVIEW = "PERIODIC_REVIEW";
    public static final String INITIAL_ASSESSMENT = "INITIAL_ASSESSMENT";

    /**
     * Opens a home-visit task for the patient's assigned CHW. No-op if the
     * patient has no CHW or an OPEN task with the same trigger already exists.
     *
     * Runs in its own transaction (REQUIRES_NEW): the partial unique index is a
     * concurrency backstop, and if a racing insert trips it the resulting
     * rollback stays contained here instead of poisoning the caller's
     * transaction (recordVisit, the schedulers, confirmPatient, …).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createTask(Patient patient, String triggerType, String reason) {
        if (patient == null || patient.getChw() == null) return;
        if (taskRepository.existsByPatientIdAndTriggerTypeAndStatus(patient.getId(), triggerType, "OPEN")) {
            return;
        }
        try {
            taskRepository.save(HomeVisitTask.builder()
                    .patient(patient)
                    .chw(patient.getChw())
                    .triggerType(triggerType)
                    .reason(reason)
                    .status("OPEN")
                    .build());
        } catch (DataIntegrityViolationException dup) {
            // A concurrent trigger raced us to the same OPEN task — the partial
            // unique index rejected the duplicate. Idempotent: nothing to do.
            log.debug("Duplicate home-visit task suppressed for patient {} trigger {}", patient.getId(), triggerType);
        }
    }

    /** Closes every OPEN task for a patient once a home visit is recorded. */
    @Transactional
    public void completeOpenTasksForPatient(UUID patientId, UUID visitId) {
        List<HomeVisitTask> open = taskRepository.findByPatientIdAndStatus(patientId, "OPEN");
        for (HomeVisitTask t : open) {
            t.setStatus("COMPLETED");
            t.setCompletedAt(LocalDateTime.now());
            t.setCompletedVisitId(visitId);
            taskRepository.save(t);
        }
    }

    /** Open tasks visible to the caller: a CHW sees their own; clinical/supervisor/admin see their facility (or all). */
    public List<HomeVisitTaskResponse> getOpenTasksForCaller() {
        SystemUser user = userRepository.findByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Your session is no longer valid. Please sign in again."));

        List<HomeVisitTask> tasks;
        if (user.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Your CHW profile could not be found. Contact your administrator."));
            tasks = taskRepository.findByChwIdAndStatusOrderByCreatedAtDesc(chw.getId(), "OPEN");
        } else if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SYSTEM_ADMIN
                || user.getRole() == UserRole.SUPERVISOR) {
            tasks = taskRepository.findByStatusOrderByCreatedAtDesc("OPEN");
        } else {
            FacilityProvider provider = facilityProviderRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Facility provider profile not found."));
            tasks = taskRepository.findByChwFacilityIdAndStatusOrderByCreatedAtDesc(
                    provider.getFacility().getId(), "OPEN");
        }
        return tasks.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** Manually mark a task done (e.g. resolved without a recorded visit). */
    @Transactional
    public void completeTask(UUID taskId) {
        HomeVisitTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found."));
        task.setStatus("COMPLETED");
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    private HomeVisitTaskResponse toResponse(HomeVisitTask t) {
        Patient p = t.getPatient();
        return HomeVisitTaskResponse.builder()
                .id(t.getId())
                .patientId(p.getId())
                .patientName(p.getFullName())
                .patientCode(p.getPatientCode())
                .village(p.getVillage())
                .diagnosisType(p.getDiagnosisType() != null ? p.getDiagnosisType().name() : null)
                .chwId(t.getChw().getId())
                .chwName(t.getChw().getUser().getFullName())
                .triggerType(t.getTriggerType())
                .reason(t.getReason())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build();
    }
}

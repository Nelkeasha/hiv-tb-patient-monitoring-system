package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.WriteAiRiskScoreRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AiRiskScoreResponse;
import com.nelly.hivtbmonitoringsystem.entity.AiRiskScore;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.AiRiskScoreRepository;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiRiskScoreService {

    private final AiRiskScoreRepository riskScoreRepository;
    private final PatientRepository patientRepository;
    private final SystemUserRepository systemUserRepository;
    private final ChwRepository chwRepository;

    /** CHW priority list — latest score per patient, highest risk first. */
    public List<AiRiskScoreResponse> getChwPriorityList() {
        Chw chw = resolveChw();
        return riskScoreRepository.findLatestScoresForChwPatients(chw.getId())
                .stream()
                .sorted(Comparator.comparing(AiRiskScore::getRiskScore).reversed())
                .map(this::toResponse)
                .toList();
    }

    /** Latest score for a specific patient. CHW must own the patient. */
    public AiRiskScoreResponse getLatestForPatient(UUID patientId) {
        authorizePatientAccess(patientId);
        return riskScoreRepository.findTopByPatientIdOrderByCalculatedAtDesc(patientId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No risk score found for this patient"));
    }

    /** Full score history for a patient, most recent first. CHW must own the patient. */
    public List<AiRiskScoreResponse> getHistoryForPatient(UUID patientId) {
        authorizePatientAccess(patientId);
        return riskScoreRepository.findByPatientIdOrderByCalculatedAtDesc(patientId)
                .stream().map(this::toResponse).toList();
    }

    /** Latest score per HIGH/CRITICAL patient across the system. FACILITY_PROVIDER and SYSTEM_ADMIN only. */
    public List<AiRiskScoreResponse> getHighRiskPatients() {
        return riskScoreRepository.findLatestHighRiskScores()
                .stream()
                .sorted(Comparator.comparing(AiRiskScore::getRiskScore).reversed())
                .map(this::toResponse)
                .toList();
    }

    /** Patient's own latest risk score. */
    public AiRiskScoreResponse getMyLatestScore() {
        SystemUser user = resolveCurrentUser();
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Patient profile not found"));
        return riskScoreRepository.findTopByPatientIdOrderByCalculatedAtDesc(patient.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No risk score available yet"));
    }

    /** Called by the Python AI microservice (SYSTEM_ADMIN credentials) to persist a new score. */
    @Transactional
    public AiRiskScoreResponse writeScore(WriteAiRiskScoreRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        AiRiskScore score = AiRiskScore.builder()
                .patient(patient)
                .riskLevel(req.getRiskLevel())
                .riskScore(req.getRiskScore())
                .suspicionScore(req.getSuspicionScore() != null ? req.getSuspicionScore() : 0)
                .missedDoses7d(req.getMissedDoses7d() != null ? req.getMissedDoses7d() : 0)
                .missedDoses14d(req.getMissedDoses14d() != null ? req.getMissedDoses14d() : 0)
                .missedDoses30d(req.getMissedDoses30d() != null ? req.getMissedDoses30d() : 0)
                .avgResponseTimeSeconds(req.getAvgResponseTimeSeconds())
                .sideEffectReports14d(req.getSideEffectReports14d() != null ? req.getSideEffectReports14d() : 0)
                .missedVisits30d(req.getMissedVisits30d() != null ? req.getMissedVisits30d() : 0)
                .timestampAnomalyDetected(req.getTimestampAnomalyDetected() != null && req.getTimestampAnomalyDetected())
                .pillCountDiscrepancyDetected(req.getPillCountDiscrepancyDetected() != null && req.getPillCountDiscrepancyDetected())
                .windowViolationDetected(req.getWindowViolationDetected() != null && req.getWindowViolationDetected())
                .recommendedAction(req.getRecommendedAction())
                .build();

        return toResponse(riskScoreRepository.save(score));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void authorizePatientAccess(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        SystemUser caller = resolveCurrentUser();
        if (caller.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(caller.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (!patient.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
            }
        }
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

    private AiRiskScoreResponse toResponse(AiRiskScore s) {
        return AiRiskScoreResponse.builder()
                .id(s.getId())
                .patientId(s.getPatient().getId())
                .patientName(s.getPatient().getFullName())
                .riskLevel(s.getRiskLevel())
                .riskScore(s.getRiskScore())
                .suspicionScore(s.getSuspicionScore())
                .missedDoses7d(s.getMissedDoses7d())
                .missedDoses14d(s.getMissedDoses14d())
                .missedDoses30d(s.getMissedDoses30d())
                .avgResponseTimeSeconds(s.getAvgResponseTimeSeconds())
                .sideEffectReports14d(s.getSideEffectReports14d())
                .missedVisits30d(s.getMissedVisits30d())
                .timestampAnomalyDetected(s.getTimestampAnomalyDetected())
                .pillCountDiscrepancyDetected(s.getPillCountDiscrepancyDetected())
                .windowViolationDetected(s.getWindowViolationDetected())
                .recommendedAction(s.getRecommendedAction())
                .calculatedAt(s.getCalculatedAt())
                .build();
    }
}

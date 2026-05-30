package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.ChwDashboardResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.ChwPriorityListResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.PriorityPatientResponse;
import com.nelly.hivtbmonitoringsystem.entity.AiRiskScore;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import com.nelly.hivtbmonitoringsystem.repository.AiRiskScoreRepository;
import com.nelly.hivtbmonitoringsystem.repository.AlertRepository;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChwDashboardService {

    private final ChwRepository chwRepository;
    private final PatientRepository patientRepository;
    private final AiRiskScoreRepository riskScoreRepository;
    private final AlertRepository alertRepository;
    private final SystemUserRepository systemUserRepository;

    public ChwDashboardResponse getDashboard() {
        Chw chw = resolveChw();

        List<AiRiskScore> latestScores = riskScoreRepository.findLatestScoresForChwPatients(chw.getId());
        Map<UUID, AiRiskScore> scoreByPatient = latestScores.stream()
                .collect(Collectors.toMap(s -> s.getPatient().getId(), Function.identity()));

        List<Patient> patients = patientRepository.findByChwIdAndIsActiveTrue(chw.getId());

        int visitToday = 0, callToday = 0, stable = 0;
        for (Patient p : patients) {
            AiRiskScore score = scoreByPatient.get(p.getId());
            RiskLevel level = score != null ? score.getRiskLevel() : null;
            if (level == RiskLevel.CRITICAL || level == RiskLevel.HIGH) visitToday++;
            else if (level == RiskLevel.MODERATE) callToday++;
            else stable++;
        }

        int activeAlerts = alertRepository.findByChwIdAndIsResolvedFalseOrderByCreatedAtDesc(chw.getId()).size();

        return ChwDashboardResponse.builder()
                .totalPatients(patients.size())
                .visitTodayCount(visitToday)
                .callTodayCount(callToday)
                .stableCount(stable)
                .activeAlerts(activeAlerts)
                .chwName(chw.getUser().getFullName())
                .chwCode(chw.getEmployeeCode())
                .build();
    }

    public ChwPriorityListResponse getPriorityList() {
        Chw chw = resolveChw();

        List<AiRiskScore> latestScores = riskScoreRepository.findLatestScoresForChwPatients(chw.getId());
        Map<UUID, AiRiskScore> scoreByPatient = latestScores.stream()
                .collect(Collectors.toMap(s -> s.getPatient().getId(), Function.identity()));

        List<Patient> patients = patientRepository.findByChwIdAndIsActiveTrue(chw.getId());

        List<PriorityPatientResponse> visitToday = patients.stream()
                .filter(p -> {
                    AiRiskScore s = scoreByPatient.get(p.getId());
                    return s != null && (s.getRiskLevel() == RiskLevel.CRITICAL || s.getRiskLevel() == RiskLevel.HIGH);
                })
                .sorted(Comparator.comparing(p -> {
                    AiRiskScore s = scoreByPatient.get(p.getId());
                    return s.getRiskScore();
                }, Comparator.reverseOrder()))
                .map(p -> toPatientResponse(p, scoreByPatient.get(p.getId()), "VISIT_TODAY"))
                .toList();

        List<PriorityPatientResponse> callToday = patients.stream()
                .filter(p -> {
                    AiRiskScore s = scoreByPatient.get(p.getId());
                    return s != null && s.getRiskLevel() == RiskLevel.MODERATE;
                })
                .sorted(Comparator.comparing(p -> {
                    AiRiskScore s = scoreByPatient.get(p.getId());
                    return s.getRiskScore();
                }, Comparator.reverseOrder()))
                .map(p -> toPatientResponse(p, scoreByPatient.get(p.getId()), "CALL_TODAY"))
                .toList();

        List<PriorityPatientResponse> stable = patients.stream()
                .filter(p -> {
                    AiRiskScore s = scoreByPatient.get(p.getId());
                    return s == null || s.getRiskLevel() == RiskLevel.LOW;
                })
                .map(p -> toPatientResponse(p, scoreByPatient.get(p.getId()), "STABLE"))
                .toList();

        return ChwPriorityListResponse.builder()
                .chwId(chw.getId())
                .generatedAt(LocalDateTime.now())
                .visitToday(visitToday)
                .callToday(callToday)
                .stable(stable)
                .totalPatients(patients.size())
                .build();
    }

    private PriorityPatientResponse toPatientResponse(Patient p, AiRiskScore score, String group) {
        return PriorityPatientResponse.builder()
                .patientId(p.getId())
                .patientName(p.getFullName())
                .patientCode(p.getPatientCode())
                .village(p.getVillage())
                .riskScore(score != null ? score.getRiskScore() : null)
                .riskLevel(score != null ? score.getRiskLevel().name() : "LOW")
                .priorityGroup(group)
                .recommendedAction(score != null ? score.getRecommendedAction() : null)
                .build();
    }

    private Chw resolveChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
    }
}

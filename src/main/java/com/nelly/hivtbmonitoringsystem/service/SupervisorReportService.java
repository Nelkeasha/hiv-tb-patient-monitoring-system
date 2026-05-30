package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorChwReportRow;
import com.nelly.hivtbmonitoringsystem.dto.response.SupervisorReportResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupervisorReportService {

    private final SupervisorRepository supervisorRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final AlertRepository alertRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final SystemUserRepository systemUserRepository;

    public SupervisorReportResponse generateSummary() {
        Supervisor supervisor = resolveSupervisor();
        UUID facilityId = supervisor.getFacility().getId();

        // ── Workforce ─────────────────────────────────────────────────────────
        List<Chw> allChws = chwRepository.findByFacilityId(facilityId);
        long activeChws = allChws.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive())).count();

        // ── Patients ──────────────────────────────────────────────────────────
        List<Patient> activePatients = patientRepository.findByFacilityIdAndIsActiveTrue(facilityId);
        long hivOnly = count(activePatients, p -> p.getDiagnosisType() == DiagnosisType.HIV);
        long tbOnly  = count(activePatients, p -> p.getDiagnosisType() == DiagnosisType.TB);
        long hivTb   = count(activePatients, p -> p.getDiagnosisType() == DiagnosisType.HIV_TB_COINFECTION);

        // ── Risk distribution ─────────────────────────────────────────────────
        Set<UUID> patientIds = activePatients.stream()
                .map(Patient::getId).collect(Collectors.toSet());

        Map<UUID, RiskLevel> riskByPatient = aiRiskScoreRepository
                .findLatestScoresForFacilityPatients(facilityId).stream()
                .filter(s -> patientIds.contains(s.getPatient().getId()))
                .collect(Collectors.toMap(s -> s.getPatient().getId(),
                        AiRiskScore::getRiskLevel, (a, b) -> a));

        long riskLow      = countRisk(riskByPatient, RiskLevel.LOW);
        long riskModerate = countRisk(riskByPatient, RiskLevel.MODERATE);
        long riskHigh     = countRisk(riskByPatient, RiskLevel.HIGH);
        long riskCritical = countRisk(riskByPatient, RiskLevel.CRITICAL);
        long riskUnscored = activePatients.size() - riskByPatient.size();

        // ── Adherence ─────────────────────────────────────────────────────────
        List<MedicationRecord> medRecords = medicationRecordRepository.findByFacilityId(facilityId);
        OptionalDouble avg = medRecords.stream()
                .mapToDouble(r -> r.getAdherencePct().doubleValue()).average();
        BigDecimal adherenceAvg = avg.isPresent()
                ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long belowThreshold = medRecords.stream()
                .map(r -> r.getPatient().getId()).distinct()
                .filter(id -> medRecords.stream()
                        .filter(r -> r.getPatient().getId().equals(id))
                        .anyMatch(r -> Boolean.TRUE.equals(r.getBelowThreshold())))
                .count();

        // ── Activity (last 30 / 7 days) ───────────────────────────────────────
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        long totalVisits30d = allChws.stream()
                .mapToLong(c -> homeVisitRepository.countByChwIdAndVisitDateAfter(c.getId(), thirtyDaysAgo))
                .sum();
        long totalMissed7d = allChws.stream()
                .mapToLong(c -> confirmationLogRepository.countMissedDosesByChwSince(c.getId(), sevenDaysAgo))
                .sum();

        // ── Alerts ────────────────────────────────────────────────────────────
        List<Alert> facilityAlerts = alertRepository.findByPatientFacilityId(facilityId);
        List<Alert> unresolved = facilityAlerts.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsResolved())).toList();

        long criticalAlerts  = countBySeverity(unresolved, AlertSeverity.CRITICAL);
        long warningAlerts   = countBySeverity(unresolved, AlertSeverity.WARNING);
        long missedDoseAlerts = countByType(unresolved, AlertType.MISSED_DOSE);
        long earlyWarnings   = countByType(unresolved, AlertType.EARLY_WARNING);

        // ── CHW performance ───────────────────────────────────────────────────
        List<SupervisorChwReportRow> chwPerformance = allChws.stream()
                .map(chw -> {
                    long activeCount = patientRepository.findByChwIdAndIsActiveTrue(chw.getId()).size();
                    long highRisk = aiRiskScoreRepository.findLatestScoresForChwPatients(chw.getId()).stream()
                            .filter(s -> s.getRiskLevel() == RiskLevel.HIGH
                                    || s.getRiskLevel() == RiskLevel.CRITICAL)
                            .count();
                    long visits = homeVisitRepository.countByChwIdAndVisitDateAfter(chw.getId(), thirtyDaysAgo);
                    long missed = confirmationLogRepository.countMissedDosesByChwSince(chw.getId(), sevenDaysAgo);
                    return SupervisorChwReportRow.builder()
                            .chwName(chw.getUser().getFullName())
                            .employeeCode(chw.getEmployeeCode())
                            .assignedVillage(chw.getAssignedVillage())
                            .activePatients(activeCount)
                            .highRiskPatients(highRisk)
                            .homeVisits30d(visits)
                            .missedDoses7d(missed)
                            .build();
                })
                .sorted(Comparator.comparingLong(SupervisorChwReportRow::getMissedDoses7d).reversed())
                .toList();

        return SupervisorReportResponse.builder()
                .facilityName(supervisor.getFacility().getName())
                .district(supervisor.getDistrict())
                .generatedAt(LocalDateTime.now())
                .totalChws(allChws.size())
                .activeChws(activeChws)
                .totalActivePatients(activePatients.size())
                .hivOnly(hivOnly)
                .tbOnly(tbOnly)
                .hivTbCoinfection(hivTb)
                .riskLow(riskLow)
                .riskModerate(riskModerate)
                .riskHigh(riskHigh)
                .riskCritical(riskCritical)
                .riskUnscored(riskUnscored)
                .facilityAdherenceAvg(adherenceAvg)
                .belowThresholdCount(belowThreshold)
                .totalHomeVisits30d(totalVisits30d)
                .totalMissedDoses7d(totalMissed7d)
                .unresolvedAlerts(unresolved.size())
                .criticalAlerts(criticalAlerts)
                .warningAlerts(warningAlerts)
                .missedDoseAlerts(missedDoseAlerts)
                .earlyWarningAlerts(earlyWarnings)
                .chwPerformance(chwPerformance)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Supervisor resolveSupervisor() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return supervisorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Supervisor profile not found"));
    }

    private long count(List<Patient> patients, java.util.function.Predicate<Patient> pred) {
        return patients.stream().filter(pred).count();
    }

    private long countRisk(Map<UUID, RiskLevel> map, RiskLevel level) {
        return map.values().stream().filter(v -> v == level).count();
    }

    private long countBySeverity(List<Alert> alerts, AlertSeverity sev) {
        return alerts.stream().filter(a -> a.getSeverity() == sev).count();
    }

    private long countByType(List<Alert> alerts, AlertType type) {
        return alerts.stream().filter(a -> a.getAlertType() == type).count();
    }
}

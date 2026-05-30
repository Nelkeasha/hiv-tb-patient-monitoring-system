package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.ChwPerformanceRow;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportResponse;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
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
public class FacilityReportService {

    private final FacilityProviderRepository facilityProviderRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final AlertRepository alertRepository;
    private final ReferralRepository referralRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final SystemUserRepository systemUserRepository;

    public FacilityReportResponse generateSummary() {
        FacilityProvider provider = resolveProvider();
        UUID facilityId = provider.getFacility().getId();

        // ── Patients ──────────────────────────────────────────────────────────
        List<Patient> activePatients = patientRepository.findByFacilityIdAndIsActiveTrue(facilityId);
        long hivOnly = count(activePatients, p -> p.getDiagnosisType() == DiagnosisType.HIV);
        long tbOnly = count(activePatients, p -> p.getDiagnosisType() == DiagnosisType.TB);
        long hivTb = count(activePatients, p -> p.getDiagnosisType() == DiagnosisType.HIV_TB_COINFECTION);

        // ── Risk distribution ─────────────────────────────────────────────────
        Set<UUID> patientIds = activePatients.stream()
                .map(Patient::getId).collect(Collectors.toSet());

        Map<UUID, RiskLevel> riskByPatient = aiRiskScoreRepository
                .findLatestScoresForFacilityPatients(facilityId).stream()
                .filter(s -> patientIds.contains(s.getPatient().getId()))
                .collect(Collectors.toMap(s -> s.getPatient().getId(), AiRiskScore::getRiskLevel,
                        (a, b) -> a));

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

        long falseFlags = medRecords.stream()
                .filter(r -> Boolean.TRUE.equals(r.getFalseConfirmationFlag())).count();

        // ── Referrals ─────────────────────────────────────────────────────────
        List<Referral> referrals = referralRepository
                .findByPatientFacilityIdOrderByCreatedAtDesc(facilityId);

        long refPending    = countStatus(referrals, ReferralStatus.PENDING);
        long refConfirmed  = countStatus(referrals, ReferralStatus.CONFIRMED)
                           + countStatus(referrals, ReferralStatus.MODIFIED);
        long refAttended   = countStatus(referrals, ReferralStatus.ATTENDED);
        long refNotAttended = countStatus(referrals, ReferralStatus.NOT_ATTENDED);
        long refCancelled  = countStatus(referrals, ReferralStatus.CANCELLED);

        // ── Alerts ────────────────────────────────────────────────────────────
        List<Alert> facilityAlerts = alertRepository.findByPatientFacilityId(facilityId);
        List<Alert> unresolved = facilityAlerts.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsResolved())).toList();
        long criticalAlerts = unresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL).count();
        long warningAlerts  = unresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.WARNING).count();

        // ── CHW performance (last 30 days) ────────────────────────────────────
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDate thirtyDaysAgoDate = LocalDate.now().minusDays(30);

        List<ChwPerformanceRow> chwPerformance = chwRepository.findByFacilityId(facilityId).stream()
                .map(chw -> {
                    long activeCount = patientRepository.findByChwIdAndIsActiveTrue(chw.getId()).size();
                    long visits = homeVisitRepository.countByChwIdAndVisitDateAfter(chw.getId(), thirtyDaysAgo);
                    long missed = confirmationLogRepository
                            .countMissedDosesByChwSince(chw.getId(), thirtyDaysAgoDate);
                    return ChwPerformanceRow.builder()
                            .chwName(chw.getUser().getFullName())
                            .employeeCode(chw.getEmployeeCode())
                            .assignedVillage(chw.getAssignedVillage())
                            .activePatients(activeCount)
                            .visitsLast30Days(visits)
                            .missedDosesLast30Days(missed)
                            .build();
                })
                .sorted(Comparator.comparingLong(ChwPerformanceRow::getMissedDosesLast30Days).reversed())
                .toList();

        return FacilityReportResponse.builder()
                .facilityName(provider.getFacility().getName())
                .district(provider.getFacility().getDistrict())
                .generatedAt(LocalDateTime.now())
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
                .falseConfirmationFlagCount(falseFlags)
                .referralTotal(referrals.size())
                .referralPending(refPending)
                .referralConfirmed(refConfirmed)
                .referralAttended(refAttended)
                .referralNotAttended(refNotAttended)
                .referralCancelled(refCancelled)
                .unresolvedAlerts(unresolved.size())
                .criticalAlerts(criticalAlerts)
                .warningAlerts(warningAlerts)
                .chwPerformance(chwPerformance)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private FacilityProvider resolveProvider() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return facilityProviderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Facility provider profile not found"));
    }

    private long count(List<Patient> patients, java.util.function.Predicate<Patient> pred) {
        return patients.stream().filter(pred).count();
    }

    private long countRisk(Map<UUID, RiskLevel> map, RiskLevel level) {
        return map.values().stream().filter(v -> v == level).count();
    }

    private long countStatus(List<Referral> referrals, ReferralStatus status) {
        return referrals.stream().filter(r -> r.getStatus() == status).count();
    }
}

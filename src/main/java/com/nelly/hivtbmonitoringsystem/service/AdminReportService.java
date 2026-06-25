package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.AdminReportResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.FacilityReportRow;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.*;
import com.nelly.hivtbmonitoringsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final SystemUserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final AlertRepository alertRepository;

    public AdminReportResponse generateSummary() {

        // ── System users ──────────────────────────────────────────────────────
        List<SystemUser> allUsers = userRepository.findAll();
        long totalChw         = countRole(allUsers, UserRole.CHW);
        long totalProviders   = countRole(allUsers, UserRole.FACILITY_PROVIDER)
                              + countRole(allUsers, UserRole.CLINICAL_STAFF);
        long totalSupervisors = countRole(allUsers, UserRole.SUPERVISOR);
        long totalPatients    = countRole(allUsers, UserRole.PATIENT);
        long activeUsers      = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getIsActive())).count();

        // ── Facilities ────────────────────────────────────────────────────────
        List<Facility> allFacilities = facilityRepository.findAll();
        long activeFacilities = allFacilities.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive())).count();

        // ── Patients ──────────────────────────────────────────────────────────
        List<Patient> activePatients = patientRepository.findByIsActiveTrueAndRegistrationStatus("CONFIRMED");
        long hivOnly = countDiagnosis(activePatients, DiagnosisType.HIV);
        long tbOnly  = countDiagnosis(activePatients, DiagnosisType.TB);
        long hivTb   = countDiagnosis(activePatients, DiagnosisType.HIV_TB_COINFECTION);

        long fhirPending = countSync(activePatients, SyncStatus.PENDING);
        long fhirSynced  = countSync(activePatients, SyncStatus.SYNCED);
        long fhirFailed  = countSync(activePatients, SyncStatus.FAILED);

        // ── Risk distribution (latest score per active patient) ───────────────
        Map<UUID, RiskLevel> riskByPatient = new HashMap<>();
        for (Patient p : activePatients) {
            aiRiskScoreRepository.findTopByPatientIdOrderByCalculatedAtDesc(p.getId())
                    .ifPresent(s -> riskByPatient.put(p.getId(), s.getRiskLevel()));
        }
        long riskLow      = countRisk(riskByPatient, RiskLevel.LOW);
        long riskModerate = countRisk(riskByPatient, RiskLevel.MODERATE);
        long riskHigh     = countRisk(riskByPatient, RiskLevel.HIGH);
        long riskCritical = countRisk(riskByPatient, RiskLevel.CRITICAL);
        long riskUnscored = activePatients.size() - riskByPatient.size();

        // ── Adherence (system-wide) ───────────────────────────────────────────
        List<MedicationRecord> allRecords = medicationRecordRepository.findAll();
        OptionalDouble avg = allRecords.stream()
                .mapToDouble(r -> r.getAdherencePct().doubleValue()).average();
        BigDecimal adherenceAvg = avg.isPresent()
                ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long belowThreshold = allRecords.stream()
                .map(r -> r.getPatient().getId()).distinct()
                .filter(id -> allRecords.stream()
                        .filter(r -> r.getPatient().getId().equals(id))
                        .anyMatch(r -> Boolean.TRUE.equals(r.getBelowThreshold())))
                .count();
        long falseFlags = allRecords.stream()
                .filter(r -> Boolean.TRUE.equals(r.getFalseConfirmationFlag())).count();

        // ── Alerts (system-wide, unresolved) ──────────────────────────────────
        List<Alert> allUnresolved = alertRepository.findByIsResolvedFalse();
        long criticalAlerts   = allUnresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL).count();
        long warningAlerts    = allUnresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.WARNING).count();
        long missedDoseAlerts = allUnresolved.stream()
                .filter(a -> a.getAlertType() == AlertType.MISSED_DOSE).count();

        // ── LTFU stats (replaces stock section) ───────────────────────────────
        long activeLtfuAlerts   = allUnresolved.stream()
                .filter(a -> a.getAlertType() == AlertType.IIT_ESCALATED).count();
        long ltfuConfirmedAlerts = allUnresolved.stream()
                .filter(a -> a.getAlertType() == AlertType.TREATMENT_INTERRUPTED).count();
        long escalatedAlerts    = allUnresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL
                          && a.getAlertType() == AlertType.TREATMENT_INTERRUPTED).count();

        // ── Per-facility breakdown ────────────────────────────────────────────
        List<Alert> allPatientAlerts = alertRepository.findAll().stream()
                .filter(a -> a.getPatient() != null && !Boolean.TRUE.equals(a.getIsResolved()))
                .toList();

        List<FacilityReportRow> facilityBreakdown = allFacilities.stream()
                .filter(f -> Boolean.TRUE.equals(f.getIsActive()))
                .map(facility -> {
                    UUID fid = facility.getId();
                    long fPatients = activePatients.stream()
                            .filter(p -> p.getFacility().getId().equals(fid)).count();
                    long fChws = chwRepository.findByFacilityId(fid).size();

                    OptionalDouble fAvg = allRecords.stream()
                            .filter(r -> r.getPatient().getFacility().getId().equals(fid))
                            .mapToDouble(r -> r.getAdherencePct().doubleValue()).average();
                    BigDecimal fAdherence = fAvg.isPresent()
                            ? BigDecimal.valueOf(fAvg.getAsDouble()).setScale(1, RoundingMode.HALF_UP)
                            : null;

                    long fHighRisk = activePatients.stream()
                            .filter(p -> p.getFacility().getId().equals(fid))
                            .filter(p -> {
                                RiskLevel lvl = riskByPatient.get(p.getId());
                                return lvl == RiskLevel.HIGH || lvl == RiskLevel.CRITICAL;
                            }).count();

                    long fAlerts = allPatientAlerts.stream()
                            .filter(a -> a.getPatient().getFacility().getId().equals(fid)).count();

                    return FacilityReportRow.builder()
                            .facilityName(facility.getName())
                            .district(facility.getDistrict())
                            .activePatients(fPatients)
                            .totalChws(fChws)
                            .adherenceAvg(fAdherence)
                            .highRiskPatients(fHighRisk)
                            .unresolvedAlerts(fAlerts)
                            .build();
                })
                .sorted(Comparator.comparingLong(FacilityReportRow::getActivePatients).reversed())
                .toList();

        return AdminReportResponse.builder()
                .generatedAt(LocalDateTime.now())
                .totalUsers(allUsers.size())
                .totalChw(totalChw)
                .totalProviders(totalProviders)
                .totalSupervisors(totalSupervisors)
                .totalPatients(totalPatients)
                .activeUsers(activeUsers)
                .inactiveUsers(allUsers.size() - activeUsers)
                .totalFacilities(allFacilities.size())
                .activeFacilities(activeFacilities)
                .facilityBreakdown(facilityBreakdown)
                .totalActivePatients(activePatients.size())
                .hivOnly(hivOnly)
                .tbOnly(tbOnly)
                .hivTbCoinfection(hivTb)
                .fhirSyncPending(fhirPending)
                .fhirSyncSynced(fhirSynced)
                .fhirSyncFailed(fhirFailed)
                .riskLow(riskLow)
                .riskModerate(riskModerate)
                .riskHigh(riskHigh)
                .riskCritical(riskCritical)
                .riskUnscored(riskUnscored)
                .systemAdherenceAvg(adherenceAvg)
                .belowThresholdCount(belowThreshold)
                .falseConfirmationFlagCount(falseFlags)
                .unresolvedAlerts(allUnresolved.size())
                .criticalAlerts(criticalAlerts)
                .warningAlerts(warningAlerts)
                .missedDoseAlerts(missedDoseAlerts)
                .activeLtfuTasks(activeLtfuAlerts)
                .ltfuConfirmedCount(ltfuConfirmedAlerts)
                .escalatedCount(escalatedAlerts)
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private long countRole(List<SystemUser> users, UserRole role) {
        return users.stream().filter(u -> u.getRole() == role).count();
    }

    private long countDiagnosis(List<Patient> patients, DiagnosisType type) {
        return patients.stream().filter(p -> p.getDiagnosisType() == type).count();
    }

    private long countSync(List<Patient> patients, SyncStatus status) {
        return patients.stream().filter(p -> p.getSyncStatus() == status).count();
    }

    private long countRisk(Map<UUID, RiskLevel> map, RiskLevel level) {
        return map.values().stream().filter(v -> v == level).count();
    }
}

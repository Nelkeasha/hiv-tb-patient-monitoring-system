package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
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
public class SupervisorDashboardService {

    private final SupervisorRepository supervisorRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final AlertRepository alertRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final SystemUserRepository systemUserRepository;
    private final FacilityRepository facilityRepository;

    public SupervisorStatsResponse getStats() {
        Supervisor supervisor = resolveSupervisor();
        UUID facilityId = supervisor.getFacility().getId();

        List<Chw> allChws = chwRepository.findByFacilityId(facilityId);
        long activeChws = allChws.stream().filter(c -> Boolean.TRUE.equals(c.getIsActive())).count();

        long activePatients = patientRepository.findByFacilityIdAndIsActiveTrueAndRegistrationStatus(facilityId, "CONFIRMED").size();

        Set<UUID> activeFacilityPatientIds = patientRepository
                .findByFacilityIdAndIsActiveTrue(facilityId).stream()
                .map(Patient::getId).collect(Collectors.toSet());

        long highRiskPatients = aiRiskScoreRepository.findLatestScoresForFacilityPatients(facilityId).stream()
                .filter(s -> s.getRiskLevel() == RiskLevel.HIGH || s.getRiskLevel() == RiskLevel.CRITICAL)
                .count();

        long criticalAlerts = alertRepository.findBySeverityAndIsResolvedFalse(AlertSeverity.CRITICAL).stream()
                .filter(a -> a.getPatient() != null && activeFacilityPatientIds.contains(a.getPatient().getId()))
                .count();

        long pendingChwAlerts = alertRepository.findUnresolvedAlertsForFacilityChws(facilityId).size();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        long totalHomeVisits30d = allChws.stream()
                .mapToLong(c -> homeVisitRepository.countByChwIdAndVisitDateAfter(c.getId(), thirtyDaysAgo))
                .sum();

        long totalMissedDoses7d = allChws.stream()
                .mapToLong(c -> confirmationLogRepository.countMissedDosesByChwSince(c.getId(), sevenDaysAgo))
                .sum();

        OptionalDouble avg = medicationRecordRepository.findByFacilityId(facilityId).stream()
                .mapToDouble(r -> r.getAdherencePct().doubleValue())
                .average();
        BigDecimal adherenceAvg = avg.isPresent()
                ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP)
                : null;

        return SupervisorStatsResponse.builder()
                .facilityName(supervisor.getFacility().getName())
                .district(supervisor.getDistrict())
                .totalChws(allChws.size())
                .activeChws(activeChws)
                .totalActivePatients(activePatients)
                .highRiskPatients(highRiskPatients)
                .criticalAlerts(criticalAlerts)
                .pendingChwAlerts(pendingChwAlerts)
                .totalHomeVisits30d(totalHomeVisits30d)
                .totalMissedDoses7d(totalMissedDoses7d)
                .facilityAdherenceAvg(adherenceAvg)
                .build();
    }

    public List<SupervisorChwPerformanceResponse> getChwPerformance() {
        Supervisor supervisor = resolveSupervisor();
        UUID facilityId = supervisor.getFacility().getId();

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        return chwRepository.findByFacilityId(facilityId).stream()
                .map(chw -> {
                    long total  = patientRepository.findByChwId(chw.getId()).size();
                    long active = patientRepository.findByChwIdAndIsActiveTrue(chw.getId()).size();
                    long visits = homeVisitRepository.countByChwIdAndVisitDateAfter(chw.getId(), thirtyDaysAgo);
                    long missed = confirmationLogRepository.countMissedDosesByChwSince(chw.getId(), sevenDaysAgo);
                    long highRisk = aiRiskScoreRepository.findLatestScoresForChwPatients(chw.getId()).stream()
                            .filter(s -> s.getRiskLevel() == RiskLevel.HIGH || s.getRiskLevel() == RiskLevel.CRITICAL)
                            .count();

                    return SupervisorChwPerformanceResponse.builder()
                            .id(chw.getId())
                            .fullName(chw.getUser().getFullName())
                            .employeeCode(chw.getEmployeeCode())
                            .assignedVillage(chw.getAssignedVillage())
                            .assignedSector(chw.getAssignedSector())
                            .totalPatients(total)
                            .activePatients(active)
                            .homeVisits30d(visits)
                            .missedDoses7d(missed)
                            .highRiskPatients(highRisk)
                            .isActive(chw.getIsActive())
                            .build();
                })
                .toList();
    }

    public SupervisorChwDetailResponse getChwDetail(UUID chwId) {
        Supervisor supervisor = resolveSupervisor();

        Chw chw = chwRepository.findById(chwId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHW not found"));

        if (!chw.getFacility().getId().equals(supervisor.getFacility().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW is not at your facility");
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        long visits30d = homeVisitRepository.countByChwIdAndVisitDateAfter(chw.getId(), thirtyDaysAgo);
        long missed7d  = confirmationLogRepository.countMissedDosesByChwSince(chw.getId(), sevenDaysAgo);

        Map<UUID, AiRiskScore> riskByPatient = aiRiskScoreRepository
                .findLatestScoresForChwPatients(chw.getId()).stream()
                .collect(Collectors.toMap(s -> s.getPatient().getId(), s -> s));

        List<FacilityPatientSummaryResponse> patients = patientRepository.findByChwId(chw.getId()).stream()
                .map(p -> {
                    AiRiskScore score = riskByPatient.get(p.getId());
                    return FacilityPatientSummaryResponse.builder()
                            .id(p.getId())
                            .patientCode(p.getPatientCode())
                            .fullName(p.getFullName())
                            .diagnosisType(p.getDiagnosisType())
                            .isActive(p.getIsActive())
                            .chwName(chw.getUser().getFullName())
                            .riskLevel(score != null ? score.getRiskLevel() : null)
                            .riskScore(score != null ? score.getRiskScore() : null)
                            .recommendedAction(score != null ? score.getRecommendedAction() : null)
                            .build();
                })
                .sorted(Comparator.comparing(
                        FacilityPatientSummaryResponse::getRiskScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        List<HomeVisitResponse> recentVisits = homeVisitRepository
                .findByChwIdOrderByVisitDateDesc(chw.getId()).stream()
                .limit(10)
                .map(this::toHomeVisitResponse)
                .toList();

        return SupervisorChwDetailResponse.builder()
                .id(chw.getId())
                .fullName(chw.getUser().getFullName())
                .employeeCode(chw.getEmployeeCode())
                .assignedVillage(chw.getAssignedVillage())
                .assignedSector(chw.getAssignedSector())
                .isActive(chw.getIsActive())
                .homeVisits30d(visits30d)
                .missedDoses7d(missed7d)
                .patients(patients)
                .recentHomeVisits(recentVisits)
                .build();
    }

    public List<FacilityPatientSummaryResponse> getHighRiskPatients() {
        Supervisor supervisor = resolveSupervisor();
        UUID facilityId = supervisor.getFacility().getId();

        Map<UUID, Patient> patientMap = patientRepository.findByFacilityIdAndIsActiveTrueAndRegistrationStatus(facilityId, "CONFIRMED").stream()
                .collect(Collectors.toMap(Patient::getId, p -> p));

        return aiRiskScoreRepository.findLatestScoresForFacilityPatients(facilityId).stream()
                .filter(s -> s.getRiskLevel() == RiskLevel.HIGH || s.getRiskLevel() == RiskLevel.CRITICAL)
                .filter(s -> patientMap.containsKey(s.getPatient().getId()))
                .sorted(Comparator.comparing(AiRiskScore::getRiskScore).reversed())
                .map(s -> {
                    Patient p = patientMap.get(s.getPatient().getId());
                    return FacilityPatientSummaryResponse.builder()
                            .id(p.getId())
                            .patientCode(p.getPatientCode())
                            .fullName(p.getFullName())
                            .diagnosisType(p.getDiagnosisType())
                            .isActive(p.getIsActive())
                            .chwName(p.getChw().getUser().getFullName())
                            .riskLevel(s.getRiskLevel())
                            .riskScore(s.getRiskScore())
                            .recommendedAction(s.getRecommendedAction())
                            .build();
                })
                .toList();
    }

    public List<AlertResponse> getFacilityAlerts() {
        Supervisor supervisor = resolveSupervisor();
        return alertRepository.findUnresolvedAlertsForFacilityChws(supervisor.getFacility().getId()).stream()
                .map(this::toAlertResponse)
                .toList();
    }

    // ── Trend ─────────────────────────────────────────────────────────────────

    /** 7-day daily home-visits + missed-doses trend — powers the supervisor dashboard area chart. */
    public List<DailyTrendPoint> getWeeklyActivity() {
        Supervisor supervisor = resolveSupervisor();
        UUID facilityId = supervisor.getFacility().getId();
        LocalDate today = LocalDate.now();
        List<DailyTrendPoint> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String day = date.getDayOfWeek()
                    .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);

            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd   = date.plusDays(1).atStartOfDay();

            long visits = homeVisitRepository.countByFacilityAndDay(facilityId, dayStart, dayEnd);
            long missed = confirmationLogRepository.countMissedByFacilityAndDate(facilityId, date);

            result.add(DailyTrendPoint.builder()
                    .day(day).visits((int) visits).missed((int) missed)
                    .build());
        }
        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Supervisor resolveSupervisor() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return supervisorRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SYSTEM_ADMIN) {
                        Facility facility = facilityRepository.findByIsActiveTrue().stream().findFirst()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active facility configured"));
                        return Supervisor.builder().user(user).facility(facility).district(facility.getDistrict()).build();
                    }
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Supervisor profile not found");
                });
    }

    private AlertResponse toAlertResponse(Alert a) {
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

    private HomeVisitResponse toHomeVisitResponse(HomeVisit v) {
        return HomeVisitResponse.builder()
                .id(v.getId())
                .patientId(v.getPatient().getId())
                .patientName(v.getPatient().getFullName())
                .patientCode(v.getPatient().getPatientCode())
                .chwId(v.getChw().getId())
                .chwName(v.getChw().getUser().getFullName())
                .visitDate(v.getVisitDate())
                .adherenceStatus(v.getAdherenceStatus())
                .pillCountRecorded(v.getPillCountRecorded())
                .pillCountExpected(v.getPillCountExpected())
                .pillCountDiscrepancy(v.getPillCountDiscrepancy())
                .symptomsReported(v.getSymptomsReported())
                .sideEffectsReported(v.getSideEffectsReported())
                .psychosocialNotes(v.getPsychosocialNotes())
                .nextVisitDate(v.getNextVisitDate())
                .syncStatus(v.getSyncStatus().name())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

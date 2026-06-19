package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.*;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
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
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityDashboardService {

    private final FacilityProviderRepository facilityProviderRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final AlertRepository alertRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final SystemUserRepository systemUserRepository;
    private final FacilityRepository facilityRepository;

    public FacilityStatsResponse getStats() {
        FacilityProvider provider = resolveProvider();
        UUID facilityId = provider.getFacility().getId();

        List<Patient> activePatients = patientRepository.findByFacilityIdAndIsActiveTrueAndRegistrationStatus(facilityId, "CONFIRMED");
        Set<UUID> activePatientIds = activePatients.stream()
                .map(Patient::getId).collect(Collectors.toSet());

        long totalChws = chwRepository.findByFacilityId(facilityId).size();
        long activePlans = treatmentPlanRepository.countActivePlansForFacility(facilityId);

        long highRiskCount = aiRiskScoreRepository.findLatestHighRiskScores().stream()
                .filter(s -> activePatientIds.contains(s.getPatient().getId()))
                .count();

        long criticalAlerts = alertRepository.findBySeverityAndIsResolvedFalse(AlertSeverity.CRITICAL).stream()
                .filter(a -> a.getPatient() != null && activePatientIds.contains(a.getPatient().getId()))
                .count();

        long belowThreshold = medicationRecordRepository.findBelowThresholdByFacilityId(facilityId).stream()
                .map(r -> r.getPatient().getId())
                .distinct()
                .count();

        OptionalDouble avg = medicationRecordRepository.findByFacilityId(facilityId).stream()
                .mapToDouble(r -> r.getAdherencePct().doubleValue())
                .average();
        BigDecimal adherenceAvg = avg.isPresent()
                ? BigDecimal.valueOf(avg.getAsDouble()).setScale(2, RoundingMode.HALF_UP)
                : null;

        return FacilityStatsResponse.builder()
                .facilityName(provider.getFacility().getName())
                .district(provider.getFacility().getDistrict())
                .totalActivePatients(activePatients.size())
                .totalChws(totalChws)
                .activeTreatmentPlans(activePlans)
                .highRiskPatientCount(highRiskCount)
                .criticalAlertCount(criticalAlerts)
                .belowThresholdCount(belowThreshold)
                .facilityAdherenceAvg(adherenceAvg)
                .build();
    }

    public List<FacilityPatientSummaryResponse> getPatients() {
        FacilityProvider provider = resolveProvider();
        UUID facilityId = provider.getFacility().getId();

        List<Patient> patients = patientRepository.findByFacilityIdAndIsActiveTrueAndRegistrationStatus(facilityId, "CONFIRMED");

        // Build a patientId → latest risk score map in one query
        Map<UUID, AiRiskScore> riskByPatient = aiRiskScoreRepository
                .findLatestScoresForFacilityPatients(facilityId).stream()
                .collect(Collectors.toMap(s -> s.getPatient().getId(), s -> s));

        return patients.stream()
                .map(p -> {
                    AiRiskScore score = riskByPatient.get(p.getId());
                    return FacilityPatientSummaryResponse.builder()
                            .id(p.getId())
                            .patientCode(p.getPatientCode())
                            .fullName(p.getFullName())
                            .diagnosisType(p.getDiagnosisType())
                            .isActive(p.getIsActive())
                            .chwName(p.getChw().getUser().getFullName())
                            .riskLevel(score != null ? score.getRiskLevel() : null)
                            .riskScore(score != null ? score.getRiskScore() : null)
                            .recommendedAction(score != null ? score.getRecommendedAction() : null)
                            .build();
                })
                .sorted(Comparator.comparing(
                        FacilityPatientSummaryResponse::getRiskScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public FacilityPatientDetailResponse getPatientDetail(UUID patientId) {
        FacilityProvider provider = resolveProvider();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (!patient.getFacility().getId().equals(provider.getFacility().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not registered at your facility");
        }

        AiRiskScore latestScore = aiRiskScoreRepository
                .findTopByPatientIdOrderByCalculatedAtDesc(patientId).orElse(null);

        List<AlertResponse> unresolvedAlerts = alertRepository
                .findByPatientIdAndIsResolvedFalseOrderByCreatedAtDesc(patientId).stream()
                .map(this::toAlertResponse)
                .toList();

        List<HomeVisitResponse> recentVisits = homeVisitRepository
                .findByPatientIdOrderByVisitDateDesc(patientId).stream()
                .limit(5)
                .map(this::toHomeVisitResponse)
                .toList();

        return FacilityPatientDetailResponse.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .sex(patient.getSex())
                .phoneNumber(patient.getPhoneNumber())
                .hasSmartphone(patient.getHasSmartphone())
                .diagnosisType(patient.getDiagnosisType())
                .artStartDate(patient.getArtStartDate())
                .tbTreatmentStartDate(patient.getTbTreatmentStartDate())
                .householdLocation(patient.getHouseholdLocation())
                .village(patient.getVillage())
                .district(patient.getDistrict())
                .isActive(patient.getIsActive())
                .registrationStatus(patient.getRegistrationStatus())
                .referralId(patient.getReferralId())
                .suspectedCondition(patient.getSuspectedCondition())
                .screeningNotes(patient.getScreeningNotes())
                .confirmedAt(patient.getConfirmedAt())
                .chwName(patient.getChw().getUser().getFullName())
                .latestRiskScore(latestScore != null ? toRiskScoreResponse(latestScore) : null)
                .unresolvedAlerts(unresolvedAlerts)
                .recentHomeVisits(recentVisits)
                .build();
    }

    public List<FacilityChwSummaryResponse> getChws() {
        FacilityProvider provider = resolveProvider();
        UUID facilityId = provider.getFacility().getId();

        return chwRepository.findByFacilityId(facilityId).stream()
                .map(chw -> {
                    long total  = patientRepository.findByChwId(chw.getId()).size();
                    long active = patientRepository.findByChwIdAndIsActiveTrue(chw.getId()).size();
                    return FacilityChwSummaryResponse.builder()
                            .id(chw.getId())
                            .fullName(chw.getUser().getFullName())
                            .employeeCode(chw.getEmployeeCode())
                            .assignedVillage(chw.getAssignedVillage())
                            .assignedSector(chw.getAssignedSector())
                            .totalPatients(total)
                            .activePatients(active)
                            .isActive(chw.getIsActive())
                            .build();
                })
                .toList();
    }

    public List<FacilityPatientSummaryResponse> getBelowThresholdPatients() {
        FacilityProvider provider = resolveProvider();
        UUID facilityId = provider.getFacility().getId();

        Map<UUID, AiRiskScore> riskByPatient = aiRiskScoreRepository
                .findLatestScoresForFacilityPatients(facilityId).stream()
                .collect(Collectors.toMap(s -> s.getPatient().getId(), s -> s));

        return medicationRecordRepository.findBelowThresholdByFacilityId(facilityId).stream()
                .map(MedicationRecord::getPatient)
                .distinct()
                .map(p -> {
                    AiRiskScore score = riskByPatient.get(p.getId());
                    return FacilityPatientSummaryResponse.builder()
                            .id(p.getId())
                            .patientCode(p.getPatientCode())
                            .fullName(p.getFullName())
                            .diagnosisType(p.getDiagnosisType())
                            .isActive(p.getIsActive())
                            .chwName(p.getChw().getUser().getFullName())
                            .riskLevel(score != null ? score.getRiskLevel() : null)
                            .riskScore(score != null ? score.getRiskScore() : null)
                            .recommendedAction(score != null ? score.getRecommendedAction() : null)
                            .build();
                })
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** 7-day daily adherence trend for the facility — used by the clinical dashboard chart. */
    public List<DailyTrendPoint> getAdherenceTrend() {
        FacilityProvider provider = resolveProvider();
        UUID facilityId = provider.getFacility().getId();
        LocalDate today = LocalDate.now();
        List<DailyTrendPoint> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String day = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, java.util.Locale.ENGLISH);

            List<com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog> logs =
                    confirmationLogRepository.findByFacilityIdAndScheduledDate(facilityId, date);

            int total     = logs.size();
            int notMissed = (int) logs.stream().filter(cl -> !Boolean.TRUE.equals(cl.getIsMissed())).count();
            int inWindow  = (int) logs.stream()
                    .filter(cl -> Boolean.TRUE.equals(cl.getIsWithinWindow())).count();

            int adherence = total > 0 ? Math.round(notMissed * 100f / total) : 0;
            int confirmed = total > 0 ? Math.round(inWindow  * 100f / total) : 0;

            result.add(DailyTrendPoint.builder()
                    .day(day).adherence(adherence).confirmed(confirmed)
                    .build());
        }
        return result;
    }

    private FacilityProvider resolveProvider() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return facilityProviderRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SYSTEM_ADMIN) {
                        Facility facility = facilityRepository.findByIsActiveTrue().stream().findFirst()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active facility configured"));
                        return FacilityProvider.builder().user(user).facility(facility).build();
                    }
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Facility provider profile not found");
                });
    }

    private AiRiskScoreResponse toRiskScoreResponse(AiRiskScore s) {
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

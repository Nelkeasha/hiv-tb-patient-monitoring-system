package com.nelly.hivtbmonitoringsystem.service.report;

import com.nelly.hivtbmonitoringsystem.dto.report.Indicator;
import com.nelly.hivtbmonitoringsystem.dto.report.KvSection;
import com.nelly.hivtbmonitoringsystem.dto.report.LineListing;
import com.nelly.hivtbmonitoringsystem.dto.report.Recommendation;
import com.nelly.hivtbmonitoringsystem.dto.report.ReportModel;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import com.nelly.hivtbmonitoringsystem.enums.RiskLevel;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Assembles the Facility (clinical provider) tier's {@link ReportModel} for a
 * chosen reporting period. Same engine as {@link SupervisorReportModelService},
 * scoped to the signed-in facility provider's facility and extended with a
 * referral funnel.
 */
@Service
@RequiredArgsConstructor
public class FacilityReportModelService {

    private static final DateTimeFormatter D  = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter DY = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final double ADHERENCE_TARGET = 90.0;
    private static final double ADHERENCE_FLOOR  = 80.0;
    private static final int    LINE_LIST_CAP    = 25;

    private final FacilityProviderRepository facilityProviderRepository;
    private final SystemUserRepository systemUserRepository;
    private final ChwRepository chwRepository;
    private final PatientRepository patientRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final AlertRepository alertRepository;
    private final ReferralRepository referralRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final ConfirmationLogRepository confirmationLogRepository;

    public ReportModel build(LocalDate fromParam, LocalDate toParam) {
        Ctx c = new Ctx();

        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = systemUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        FacilityProvider provider = facilityProviderRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Facility provider profile not found"));
        UUID facilityId = provider.getFacility().getId();
        c.facilityName = provider.getFacility().getName();
        c.district = provider.getFacility().getDistrict();
        c.generatedBy = user.getFullName();

        // ── Period maths ────────────────────────────────────────────────────────
        c.to = (toParam != null) ? toParam : LocalDate.now();
        c.from = (fromParam != null) ? fromParam : c.to.minusDays(29);
        if (c.from.isAfter(c.to)) { LocalDate t = c.from; c.from = c.to; c.to = t; }
        c.windowDays = ChronoUnit.DAYS.between(c.from, c.to) + 1;
        c.prevTo = c.from.minusDays(1);
        c.prevFrom = c.prevTo.minusDays(c.windowDays - 1);
        c.periodLabel = c.from.format(D) + " - " + c.to.format(DY);
        c.comparisonLabel = "vs previous " + c.windowDays + " days ("
                + c.prevFrom.format(D) + " - " + c.prevTo.format(D) + ")";

        // ── Patients ────────────────────────────────────────────────────────────
        List<Patient> active = patientRepository
                .findByFacilityIdAndIsActiveTrueAndRegistrationStatus(facilityId, "CONFIRMED");
        c.txCurr = active.size();
        c.hivOnly = active.stream().filter(p -> p.getDiagnosisType() == DiagnosisType.HIV).count();
        c.tbOnly  = active.stream().filter(p -> p.getDiagnosisType() == DiagnosisType.TB).count();
        c.coinf   = active.stream().filter(p -> p.getDiagnosisType() == DiagnosisType.HIV_TB_COINFECTION).count();
        Map<UUID, Patient> byId = active.stream().collect(Collectors.toMap(Patient::getId, p -> p, (a, b) -> a));
        c.newEnrolCur  = active.stream().filter(p -> inWindow(p.getConfirmedAt(), c.from, c.to)).count();
        c.newEnrolPrev = active.stream().filter(p -> inWindow(p.getConfirmedAt(), c.prevFrom, c.prevTo)).count();

        // ── Risk distribution ───────────────────────────────────────────────────
        Set<UUID> activeIds = byId.keySet();
        Map<UUID, RiskLevel> riskByPatient = aiRiskScoreRepository
                .findLatestScoresForFacilityPatients(facilityId).stream()
                .filter(s -> activeIds.contains(s.getPatient().getId()))
                .collect(Collectors.toMap(s -> s.getPatient().getId(), AiRiskScore::getRiskLevel, (a, b) -> a));
        c.riskLow      = countRisk(riskByPatient, RiskLevel.LOW);
        c.riskModerate = countRisk(riskByPatient, RiskLevel.MODERATE);
        c.riskHigh     = countRisk(riskByPatient, RiskLevel.HIGH);
        c.riskCritical = countRisk(riskByPatient, RiskLevel.CRITICAL);
        c.riskUnscored = Math.max(0, c.txCurr - riskByPatient.size());

        // ── Adherence ───────────────────────────────────────────────────────────
        List<MedicationRecord> med = medicationRecordRepository.findByFacilityId(facilityId);
        c.adhCur  = windowAdherence(med, c.from, c.to);
        c.adhPrev = windowAdherence(med, c.prevFrom, c.prevTo);
        Map<UUID, List<MedicationRecord>> curByPatient = med.stream()
                .filter(r -> inWindow(r.getPeriodStart(), c.from, c.to))
                .collect(Collectors.groupingBy(r -> r.getPatient().getId()));
        c.patientsMeasured = curByPatient.size();
        for (Map.Entry<UUID, List<MedicationRecord>> e : curByPatient.entrySet()) {
            double mean = e.getValue().stream().mapToDouble(r -> r.getAdherencePct().doubleValue()).average().orElse(0);
            if (mean >= ADHERENCE_FLOOR) c.patientsAtOrAbove80++;
            else c.lowAdherencePatients.add(new PatientAdh(mean, e.getValue()));
        }
        c.belowThresholdPatients = c.lowAdherencePatients.size();
        c.lowAdherencePatients.sort(Comparator.comparingDouble((PatientAdh pa) -> pa.mean));

        // ── Activity ────────────────────────────────────────────────────────────
        c.missedCur  = sumMissed(facilityId, c.from, c.to);
        c.missedPrev = sumMissed(facilityId, c.prevFrom, c.prevTo);
        c.visitsCur  = sumVisits(facilityId, c.from, c.to);
        c.visitsPrev = sumVisits(facilityId, c.prevFrom, c.prevTo);

        // ── Referrals (all-time funnel) ─────────────────────────────────────────
        List<Referral> referrals = referralRepository.findByPatientFacilityIdOrderByCreatedAtDesc(facilityId);
        c.refTotal       = referrals.size();
        c.refPending     = countStatus(referrals, ReferralStatus.PENDING);
        c.refConfirmed   = countStatus(referrals, ReferralStatus.CONFIRMED) + countStatus(referrals, ReferralStatus.MODIFIED);
        c.refAttended    = countStatus(referrals, ReferralStatus.ATTENDED);
        c.refNotAttended = countStatus(referrals, ReferralStatus.NOT_ATTENDED);
        c.refCancelled   = countStatus(referrals, ReferralStatus.CANCELLED);

        // ── Alerts ──────────────────────────────────────────────────────────────
        List<Alert> facilityAlerts = alertRepository.findByPatientFacilityId(facilityId);
        List<Alert> unresolved = facilityAlerts.stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsResolved())).toList();
        c.unresolvedTotal    = unresolved.size();
        c.unresolvedCritical = unresolved.stream().filter(a -> a.getSeverity() == AlertSeverity.CRITICAL).count();
        c.unresolvedWarning  = unresolved.stream().filter(a -> a.getSeverity() == AlertSeverity.WARNING).count();
        c.missedDoseAlerts   = unresolved.stream().filter(a -> a.getAlertType() == AlertType.MISSED_DOSE).count();
        c.earlyWarnAlerts    = unresolved.stream().filter(a -> a.getAlertType() == AlertType.EARLY_WARNING).count();
        c.alertIncidenceCur  = facilityAlerts.stream().filter(a -> inWindow(a.getCreatedAt(), c.from, c.to)).count();
        c.criticalAlerts = unresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL)
                .sorted(Comparator.comparing(Alert::getCreatedAt))
                .toList();

        // ── High/critical risk line list + un-visited count ─────────────────────
        LocalDateTime visitCutoff = LocalDate.now().minusDays(30).atStartOfDay();
        for (Map.Entry<UUID, RiskLevel> e : riskByPatient.entrySet()) {
            if (e.getValue() != RiskLevel.HIGH && e.getValue() != RiskLevel.CRITICAL) continue;
            Patient p = byId.get(e.getKey());
            if (p == null) continue;
            boolean visited = homeVisitRepository.countByPatientIdAndVisitDateAfter(p.getId(), visitCutoff) > 0;
            if (!visited) c.highRiskUnvisited++;
            c.highRiskPatients.add(new RiskRow(p, e.getValue(), visited));
        }
        c.highRiskPatients.sort(Comparator.comparingInt((RiskRow r) -> r.level == RiskLevel.CRITICAL ? 0 : 1)
                .thenComparing((RiskRow r) -> r.visited));

        // ── CHW performance (last 30 days) ──────────────────────────────────────
        LocalDateTime thirtyAgo = LocalDateTime.now().minusDays(30);
        LocalDate thirtyAgoDate = LocalDate.now().minusDays(30);
        for (Chw chw : chwRepository.findByFacilityId(facilityId)) {
            long caseload = patientRepository.countByChwIdAndIsActiveTrue(chw.getId());
            long highRisk = aiRiskScoreRepository.findLatestScoresForChwPatients(chw.getId()).stream()
                    .filter(s -> s.getRiskLevel() == RiskLevel.HIGH || s.getRiskLevel() == RiskLevel.CRITICAL).count();
            long visits = homeVisitRepository.countByChwIdAndVisitDateAfter(chw.getId(), thirtyAgo);
            long missed = confirmationLogRepository.countMissedDosesByChwSince(chw.getId(), thirtyAgoDate);
            c.chwRows.add(new ChwRow(chw, caseload, highRisk, visits, missed));
            c.maxCaseload = Math.max(c.maxCaseload, caseload);
        }
        c.chwRows.sort(Comparator.comparingLong((ChwRow r) -> r.missed).reversed());
        c.meanCaseload = c.chwRows.isEmpty() ? 0 : (double) c.txCurr / c.chwRows.size();

        return ReportModel.builder()
                .reportTitle("Facility Clinical Report")
                .scopeName(c.facilityName)
                .subScope(c.district)
                .periodLabel(c.periodLabel)
                .comparisonLabel(c.comparisonLabel)
                .generatedAt(LocalDateTime.now())
                .generatedBy(c.generatedBy)
                .executiveSummary(buildExecutiveSummary(c))
                .indicators(buildIndicators(c))
                .kvSections(buildKvSections(c))
                .lineListings(buildLineListings(c))
                .recommendations(buildRecommendations(c))
                .build();
    }

    // ── Indicators ──────────────────────────────────────────────────────────────
    private List<Indicator> buildIndicators(Ctx c) {
        List<Indicator> list = new ArrayList<>();

        list.add(Indicator.builder()
                .label("Currently on Treatment").value(String.valueOf(c.txCurr))
                .trend(Indicator.Trend.NONE).status(Indicator.Rag.NEUTRAL).target("TX_CURR").build());

        Indicator.Rag adhRag = Double.isNaN(c.adhCur) ? Indicator.Rag.NEUTRAL
                : c.adhCur >= ADHERENCE_TARGET ? Indicator.Rag.GOOD
                : c.adhCur >= ADHERENCE_FLOOR ? Indicator.Rag.WATCH : Indicator.Rag.BAD;
        list.add(Indicator.builder()
                .label("Mean Adherence").value(pct(c.adhCur))
                .previousValue(pct(c.adhPrev)).deltaLabel(deltaPts(c.adhCur, c.adhPrev))
                .trend(trendHigherBetter(c.adhCur, c.adhPrev)).status(adhRag)
                .target("Target >=90%").build());

        double pctAdequate = c.patientsMeasured > 0 ? 100.0 * c.patientsAtOrAbove80 / c.patientsMeasured : Double.NaN;
        list.add(Indicator.builder()
                .label("Patients >=80% Adherent").value(pct(pctAdequate))
                .trend(Indicator.Trend.NONE)
                .status(Double.isNaN(pctAdequate) ? Indicator.Rag.NEUTRAL
                        : pctAdequate >= 85 ? Indicator.Rag.GOOD
                        : pctAdequate >= 70 ? Indicator.Rag.WATCH : Indicator.Rag.BAD)
                .target(c.patientsMeasured + " measured").build());

        list.add(Indicator.builder()
                .label("New Enrolments").value(String.valueOf(c.newEnrolCur))
                .previousValue(String.valueOf(c.newEnrolPrev)).deltaLabel(deltaCount(c.newEnrolCur, c.newEnrolPrev))
                .trend(trendHigherBetter(c.newEnrolCur, c.newEnrolPrev)).status(Indicator.Rag.NEUTRAL)
                .target("TX_NEW").build());

        list.add(Indicator.builder()
                .label("Missed Doses").value(String.valueOf(c.missedCur))
                .previousValue(String.valueOf(c.missedPrev)).deltaLabel(deltaCount(c.missedCur, c.missedPrev))
                .trend(trendHigherBetter(c.missedCur, c.missedPrev))
                .status(c.missedCur > c.missedPrev ? Indicator.Rag.WATCH : Indicator.Rag.GOOD)
                .target("period").build());

        list.add(Indicator.builder()
                .label("Home Visits").value(String.valueOf(c.visitsCur))
                .previousValue(String.valueOf(c.visitsPrev)).deltaLabel(deltaCount(c.visitsCur, c.visitsPrev))
                .trend(trendHigherBetter(c.visitsCur, c.visitsPrev))
                .status(c.visitsCur >= c.visitsPrev ? Indicator.Rag.GOOD : Indicator.Rag.WATCH)
                .target("period").build());

        list.add(Indicator.builder()
                .label("Unresolved Critical Alerts").value(String.valueOf(c.unresolvedCritical))
                .trend(Indicator.Trend.NONE)
                .status(c.unresolvedCritical > 0 ? Indicator.Rag.BAD : Indicator.Rag.GOOD)
                .target("open now").build());

        long refDecided = c.refAttended + c.refNotAttended;
        double refCompletion = refDecided > 0 ? 100.0 * c.refAttended / refDecided : Double.NaN;
        list.add(Indicator.builder()
                .label("Referral Completion").value(pct(refCompletion))
                .trend(Indicator.Trend.NONE)
                .status(Double.isNaN(refCompletion) ? Indicator.Rag.NEUTRAL
                        : refCompletion >= 80 ? Indicator.Rag.GOOD
                        : refCompletion >= 60 ? Indicator.Rag.WATCH : Indicator.Rag.BAD)
                .target(c.refPending + " pending").build());

        return list;
    }

    private List<KvSection> buildKvSections(Ctx c) {
        List<KvSection> list = new ArrayList<>();
        list.add(KvSection.builder().title("Patient Mix").rows(new String[][]{
                {"HIV only", String.valueOf(c.hivOnly)},
                {"TB only", String.valueOf(c.tbOnly)},
                {"HIV + TB co-infection", String.valueOf(c.coinf)},
        }).build());
        list.add(KvSection.builder().title("Risk Distribution").rows(new String[][]{
                {"Low", String.valueOf(c.riskLow)},
                {"Moderate", String.valueOf(c.riskModerate)},
                {"High", String.valueOf(c.riskHigh)},
                {"Critical", String.valueOf(c.riskCritical)},
                {"Unscored", String.valueOf(c.riskUnscored)},
        }).build());
        list.add(KvSection.builder().title("Referral Funnel (All-Time)").rows(new String[][]{
                {"Total", String.valueOf(c.refTotal)},
                {"Pending", String.valueOf(c.refPending)},
                {"Confirmed", String.valueOf(c.refConfirmed)},
                {"Attended", String.valueOf(c.refAttended)},
                {"Not attended", String.valueOf(c.refNotAttended)},
                {"Cancelled", String.valueOf(c.refCancelled)},
        }).build());
        list.add(KvSection.builder().title("Alert Backlog (Unresolved)").rows(new String[][]{
                {"Total", String.valueOf(c.unresolvedTotal)},
                {"Critical", String.valueOf(c.unresolvedCritical)},
                {"Warning", String.valueOf(c.unresolvedWarning)},
                {"Missed-dose", String.valueOf(c.missedDoseAlerts)},
                {"Early-warning", String.valueOf(c.earlyWarnAlerts)},
                {"Raised this period", String.valueOf(c.alertIncidenceCur)},
        }).build());
        return list;
    }

    private List<LineListing> buildLineListings(Ctx c) {
        List<LineListing> list = new ArrayList<>();

        List<String[]> lowRows = new ArrayList<>();
        for (PatientAdh pa : capped(c.lowAdherencePatients)) {
            Patient p = pa.records.isEmpty() ? null : pa.records.get(0).getPatient();
            long daysBelow = pa.records.stream().filter(r -> Boolean.TRUE.equals(r.getBelowThreshold())).count();
            lowRows.add(new String[]{code(p), diagnosis(p), pct(pa.mean), String.valueOf(daysBelow), chwName(p)});
        }
        list.add(LineListing.builder()
                .title("Patients Requiring Attention - Low Adherence (<80% this period)" + moreNote(c.lowAdherencePatients.size()))
                .headers(new String[]{"Patient Code", "Diagnosis", "Mean Adherence", "Days Below", "CHW"})
                .rows(lowRows).emptyMessage("No patients below the 80% adherence floor this period").build());

        List<String[]> riskRows = new ArrayList<>();
        for (RiskRow rr : capped(c.highRiskPatients)) {
            riskRows.add(new String[]{code(rr.patient), rr.level.name(), diagnosis(rr.patient),
                    rr.visited ? "Yes" : "No", chwName(rr.patient)});
        }
        list.add(LineListing.builder()
                .title("High & Critical Risk Patients" + moreNote(c.highRiskPatients.size()))
                .headers(new String[]{"Patient Code", "Risk", "Diagnosis", "Visited 30d", "CHW"})
                .rows(riskRows).emptyMessage("No patients currently at high or critical risk").build());

        List<String[]> alertRows = new ArrayList<>();
        for (Alert a : capped(c.criticalAlerts)) {
            long ageDays = a.getCreatedAt() == null ? 0 : ChronoUnit.DAYS.between(a.getCreatedAt(), LocalDateTime.now());
            alertRows.add(new String[]{
                    a.getPatient() != null ? code(a.getPatient()) : "-",
                    a.getAlertType() != null ? a.getAlertType().name() : "-",
                    ageDays + "d",
                    a.getCreatedAt() != null ? a.getCreatedAt().toLocalDate().format(D) : "-"});
        }
        list.add(LineListing.builder()
                .title("Unresolved Critical Alerts" + moreNote(c.criticalAlerts.size()))
                .headers(new String[]{"Patient Code", "Alert Type", "Age", "Raised"})
                .rows(alertRows).emptyMessage("No unresolved critical alerts").build());

        List<String[]> chwRows = new ArrayList<>();
        for (ChwRow r : c.chwRows) {
            chwRows.add(new String[]{
                    r.chw.getUser() != null ? r.chw.getUser().getFullName() : "-",
                    nz(r.chw.getEmployeeCode()), nz(r.chw.getAssignedVillage()),
                    String.valueOf(r.caseload), String.valueOf(r.highRisk),
                    String.valueOf(r.visits), String.valueOf(r.missed)});
        }
        list.add(LineListing.builder()
                .title("CHW Performance (Last 30 Days)")
                .headers(new String[]{"CHW Name", "Employee Code", "Village", "Active Patients", "High Risk", "Visits (30d)", "Missed Doses (30d)"})
                .rows(chwRows).emptyMessage("No CHW activity recorded").build());

        return list;
    }

    private List<Recommendation> buildRecommendations(Ctx c) {
        List<Recommendation> recs = new ArrayList<>();
        if (c.highRiskUnvisited > 0) {
            recs.add(rec(Recommendation.Severity.CRITICAL,
                    c.highRiskUnvisited + " high/critical-risk patient(s) have had no home visit in the last 30 days.",
                    "Schedule home visits for these patients this week (see High & Critical Risk list).",
                    "CHW Supervisor"));
        }
        if (c.unresolvedCritical > 0) {
            recs.add(rec(Recommendation.Severity.CRITICAL,
                    c.unresolvedCritical + " critical alert(s) remain unresolved.",
                    "Review and close each within 48 hours.", "Clinical Staff"));
        }
        if (!Double.isNaN(c.adhCur) && c.adhCur < ADHERENCE_TARGET) {
            String move = Double.isNaN(c.adhPrev) ? "" : " (" + deltaPts(c.adhCur, c.adhPrev) + " vs previous period)";
            recs.add(rec(Recommendation.Severity.WARNING,
                    "Mean adherence is " + pct(c.adhCur) + ", below the 90% programme target" + move + ".",
                    "Review the low-adherence line list and intensify DOT / counselling.", "Facility Provider"));
        }
        if (c.refPending > 0) {
            recs.add(rec(Recommendation.Severity.WARNING,
                    c.refPending + " referral(s) are still pending an outcome.",
                    "Follow up pending referrals and record attendance.", "Facility Provider"));
        }
        if (c.missedCur > 0 && c.missedCur > c.missedPrev * 1.2) {
            recs.add(rec(Recommendation.Severity.WARNING,
                    "Missed doses rose to " + c.missedCur + " this period (from " + c.missedPrev + ").",
                    "Check reminder delivery and follow up repeat missers.", "Facility Provider"));
        }
        if (c.txCurr > 0 && c.riskUnscored > 0.2 * c.txCurr) {
            recs.add(rec(Recommendation.Severity.INFO,
                    c.riskUnscored + " active patient(s) have no AI risk score.",
                    "Confirm the nightly risk-scoring job and data completeness.", "System Admin"));
        }
        if (recs.isEmpty()) {
            recs.add(rec(Recommendation.Severity.INFO,
                    "No critical issues detected this period.",
                    "Maintain routine monitoring and home-visit schedules.", "Facility Provider"));
        }
        recs.sort(Comparator.comparingInt(r -> r.getSeverity().ordinal()));
        return recs;
    }

    private List<String> buildExecutiveSummary(Ctx c) {
        List<String> out = new ArrayList<>();

        StringBuilder what = new StringBuilder();
        what.append("Over the reporting period the facility supported ").append(c.txCurr).append(" patient(s) on treatment");
        if (c.newEnrolCur > 0) what.append(", enrolling ").append(c.newEnrolCur).append(" new patient(s)");
        what.append(". ");
        if (!Double.isNaN(c.adhCur)) {
            what.append("Mean medication adherence was ").append(pct(c.adhCur));
            if (!Double.isNaN(c.adhPrev)) {
                what.append(" (").append(deltaPts(c.adhCur, c.adhPrev)).append(" ")
                    .append(c.adhCur >= c.adhPrev ? "up" : "down").append(" on the previous period)");
            }
            what.append(". ");
        }
        what.append(c.missedCur).append(" missed dose(s) and ").append(c.visitsCur).append(" home visit(s) were recorded.");
        out.add("What happened.||" + what);

        StringBuilder why = new StringBuilder();
        if (!Double.isNaN(c.adhCur) && c.adhCur < ADHERENCE_TARGET) {
            why.append("Adherence sits below the 90% programme target, raising the risk of treatment failure and resistance. ");
        } else if (!Double.isNaN(c.adhCur)) {
            why.append("Adherence is at or above the 90% programme target. ");
        }
        why.append(c.unresolvedCritical).append(" critical and ").append(c.unresolvedWarning)
           .append(" warning alert(s) are unresolved. ");
        why.append(c.refPending).append(" referral(s) await an outcome.");
        out.add("Why it matters.||" + why);

        StringBuilder who = new StringBuilder();
        who.append(c.belowThresholdPatients).append(" patient(s) fell below the 80% adherence floor this period");
        if (c.highRiskUnvisited > 0) {
            who.append("; ").append(c.highRiskUnvisited).append(" high-risk patient(s) have had no home visit in 30 days");
        }
        who.append(". Affected patients are listed by code in the case lists below.");
        out.add("Who is affected.||" + who);

        List<Recommendation> recs = buildRecommendations(c);
        StringBuilder act = new StringBuilder();
        int n = 1;
        for (Recommendation r : recs) {
            if (r.getSeverity() == Recommendation.Severity.INFO && n > 1) break;
            act.append(n++).append(") ").append(r.getAction()).append(" (").append(r.getOwner()).append("). ");
            if (n > 3) break;
        }
        out.add("What the facility should do.||" + act.toString().trim());

        return out;
    }

    // ── Data helpers ─────────────────────────────────────────────────────────────
    private long sumMissed(UUID facilityId, LocalDate from, LocalDate to) {
        long total = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            total += confirmationLogRepository.countMissedByFacilityAndDate(facilityId, d);
        }
        return total;
    }

    private long sumVisits(UUID facilityId, LocalDate from, LocalDate to) {
        long total = 0;
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            total += homeVisitRepository.countByFacilityAndDay(facilityId, d.atStartOfDay(), d.plusDays(1).atStartOfDay());
        }
        return total;
    }

    private double windowAdherence(List<MedicationRecord> med, LocalDate from, LocalDate to) {
        return med.stream().filter(r -> inWindow(r.getPeriodStart(), from, to))
                .mapToDouble(r -> r.getAdherencePct().doubleValue()).average().orElse(Double.NaN);
    }

    private static boolean inWindow(LocalDate d, LocalDate from, LocalDate to) {
        return d != null && !d.isBefore(from) && !d.isAfter(to);
    }
    private static boolean inWindow(LocalDateTime dt, LocalDate from, LocalDate to) {
        return dt != null && inWindow(dt.toLocalDate(), from, to);
    }
    private static long countRisk(Map<UUID, RiskLevel> map, RiskLevel level) {
        return map.values().stream().filter(v -> v == level).count();
    }
    private static long countStatus(List<Referral> referrals, ReferralStatus status) {
        return referrals.stream().filter(r -> r.getStatus() == status).count();
    }
    private static <T> List<T> capped(List<T> list) {
        return list.size() > LINE_LIST_CAP ? list.subList(0, LINE_LIST_CAP) : list;
    }
    private static String moreNote(int total) {
        return total > LINE_LIST_CAP ? "  (showing top " + LINE_LIST_CAP + " of " + total + ")" : "";
    }
    private static Recommendation rec(Recommendation.Severity sev, String finding, String action, String owner) {
        return Recommendation.builder().severity(sev).finding(finding).action(action).owner(owner).build();
    }
    private static String pct(double v) { return Double.isNaN(v) ? "-" : String.format("%.1f%%", v); }
    private static String deltaPts(double cur, double prev) {
        if (Double.isNaN(cur) || Double.isNaN(prev)) return null;
        return String.format("%+.1f pts", cur - prev);
    }
    private static String deltaCount(long cur, long prev) {
        long d = cur - prev; return (d >= 0 ? "+" : "") + d;
    }
    private static Indicator.Trend trendHigherBetter(double cur, double prev) {
        if (Double.isNaN(cur) || Double.isNaN(prev) || cur == prev) return Indicator.Trend.FLAT;
        return cur > prev ? Indicator.Trend.UP : Indicator.Trend.DOWN;
    }
    private static Indicator.Trend trendHigherBetter(long cur, long prev) {
        if (cur == prev) return Indicator.Trend.FLAT;
        return cur > prev ? Indicator.Trend.UP : Indicator.Trend.DOWN;
    }
    private static String code(Patient p) { return p == null ? "-" : nz(p.getPatientCode()); }
    private static String diagnosis(Patient p) {
        if (p == null || p.getDiagnosisType() == null) return "-";
        return switch (p.getDiagnosisType()) {
            case HIV -> "HIV"; case TB -> "TB"; case HIV_TB_COINFECTION -> "HIV+TB";
        };
    }
    private static String chwName(Patient p) {
        if (p == null || p.getChw() == null || p.getChw().getUser() == null) return "-";
        return p.getChw().getUser().getFullName();
    }
    private static String nz(String s) { return s == null || s.isBlank() ? "-" : s; }

    // ── Holders ──────────────────────────────────────────────────────────────────
    private static final class Ctx {
        String facilityName, district, generatedBy, periodLabel, comparisonLabel;
        LocalDate from, to, prevFrom, prevTo;
        long windowDays;
        long txCurr, hivOnly, tbOnly, coinf;
        long newEnrolCur, newEnrolPrev;
        long riskLow, riskModerate, riskHigh, riskCritical, riskUnscored;
        double adhCur, adhPrev, meanCaseload;
        long patientsMeasured, patientsAtOrAbove80, belowThresholdPatients;
        long missedCur, missedPrev, visitsCur, visitsPrev;
        long refTotal, refPending, refConfirmed, refAttended, refNotAttended, refCancelled;
        long unresolvedTotal, unresolvedCritical, unresolvedWarning, missedDoseAlerts, earlyWarnAlerts;
        long alertIncidenceCur;
        long highRiskUnvisited, maxCaseload;
        List<Alert> criticalAlerts = new ArrayList<>();
        final List<PatientAdh> lowAdherencePatients = new ArrayList<>();
        final List<RiskRow> highRiskPatients = new ArrayList<>();
        final List<ChwRow> chwRows = new ArrayList<>();
    }
    private static final class PatientAdh {
        final double mean; final List<MedicationRecord> records;
        PatientAdh(double mean, List<MedicationRecord> records) { this.mean = mean; this.records = records; }
    }
    private static final class RiskRow {
        final Patient patient; final RiskLevel level; final boolean visited;
        RiskRow(Patient patient, RiskLevel level, boolean visited) { this.patient = patient; this.level = level; this.visited = visited; }
    }
    private static final class ChwRow {
        final Chw chw; final long caseload, highRisk, visits, missed;
        ChwRow(Chw chw, long caseload, long highRisk, long visits, long missed) {
            this.chw = chw; this.caseload = caseload; this.highRisk = highRisk; this.visits = visits; this.missed = missed;
        }
    }
}

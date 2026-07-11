package com.nelly.hivtbmonitoringsystem.service.report;

import com.nelly.hivtbmonitoringsystem.dto.report.Indicator;
import com.nelly.hivtbmonitoringsystem.dto.report.KvSection;
import com.nelly.hivtbmonitoringsystem.dto.report.LineListing;
import com.nelly.hivtbmonitoringsystem.dto.report.Recommendation;
import com.nelly.hivtbmonitoringsystem.dto.report.ReportModel;
import com.nelly.hivtbmonitoringsystem.entity.*;
import com.nelly.hivtbmonitoringsystem.enums.*;
import com.nelly.hivtbmonitoringsystem.repository.*;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Assembles the system-wide Admin {@link ReportModel} for a chosen reporting
 * period. Same engine as the Supervisor/Facility tiers, but scoped to the whole
 * programme, with a facility league table in place of a CHW league table.
 */
@Service
@RequiredArgsConstructor
public class AdminReportModelService {

    private static final DateTimeFormatter D  = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter DY = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final double ADHERENCE_TARGET = 90.0;
    private static final double ADHERENCE_FLOOR  = 80.0;
    private static final int    LINE_LIST_CAP    = 25;

    private final SystemUserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final PatientRepository patientRepository;
    private final ChwRepository chwRepository;
    private final AiRiskScoreRepository aiRiskScoreRepository;
    private final MedicationRecordRepository medicationRecordRepository;
    private final AlertRepository alertRepository;
    private final HomeVisitRepository homeVisitRepository;
    private final ConfirmationLogRepository confirmationLogRepository;

    public ReportModel build(LocalDate fromParam, LocalDate toParam) {
        Ctx c = new Ctx();
        c.generatedBy = resolveCurrentUserName();

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

        // ── Users & facilities ──────────────────────────────────────────────────
        List<SystemUser> allUsers = userRepository.findAll();
        c.totalUsers   = allUsers.size();
        c.activeUsers  = allUsers.stream().filter(u -> Boolean.TRUE.equals(u.getIsActive())).count();
        c.totalChw     = countRole(allUsers, UserRole.CHW);
        c.totalProviders = countRole(allUsers, UserRole.FACILITY_PROVIDER) + countRole(allUsers, UserRole.CLINICAL_STAFF);
        c.totalSupervisors = countRole(allUsers, UserRole.SUPERVISOR);
        c.totalPatientAccounts = countRole(allUsers, UserRole.PATIENT);

        List<Facility> allFacilities = facilityRepository.findAll();
        c.totalFacilities = allFacilities.size();
        c.activeFacilities = allFacilities.stream().filter(f -> Boolean.TRUE.equals(f.getIsActive())).count();

        // ── Patients (system-wide, confirmed active) ────────────────────────────
        List<Patient> active = patientRepository.findByIsActiveTrueAndRegistrationStatus("CONFIRMED");
        c.txCurr = active.size();
        c.hivOnly = active.stream().filter(p -> p.getDiagnosisType() == DiagnosisType.HIV).count();
        c.tbOnly  = active.stream().filter(p -> p.getDiagnosisType() == DiagnosisType.TB).count();
        c.coinf   = active.stream().filter(p -> p.getDiagnosisType() == DiagnosisType.HIV_TB_COINFECTION).count();
        c.newEnrolCur  = active.stream().filter(p -> inWindow(p.getConfirmedAt(), c.from, c.to)).count();
        c.newEnrolPrev = active.stream().filter(p -> inWindow(p.getConfirmedAt(), c.prevFrom, c.prevTo)).count();

        c.fhirPending = countSync(active, SyncStatus.PENDING);
        c.fhirSynced  = countSync(active, SyncStatus.SYNCED);
        c.fhirFailed  = countSync(active, SyncStatus.FAILED);

        // ── Risk (latest score per active patient) ──────────────────────────────
        Map<UUID, RiskLevel> riskByPatient = new HashMap<>();
        for (Patient p : active) {
            aiRiskScoreRepository.findTopByPatientIdOrderByCalculatedAtDesc(p.getId())
                    .ifPresent(s -> riskByPatient.put(p.getId(), s.getRiskLevel()));
        }
        c.riskLow      = countRisk(riskByPatient, RiskLevel.LOW);
        c.riskModerate = countRisk(riskByPatient, RiskLevel.MODERATE);
        c.riskHigh     = countRisk(riskByPatient, RiskLevel.HIGH);
        c.riskCritical = countRisk(riskByPatient, RiskLevel.CRITICAL);
        c.riskUnscored = Math.max(0, c.txCurr - riskByPatient.size());

        // ── Adherence (system-wide) ─────────────────────────────────────────────
        List<MedicationRecord> med = medicationRecordRepository.findAll();
        c.adhCur  = windowAdherence(med, c.from, c.to);
        c.adhPrev = windowAdherence(med, c.prevFrom, c.prevTo);
        Map<UUID, List<MedicationRecord>> curByPatient = med.stream()
                .filter(r -> inWindow(r.getPeriodStart(), c.from, c.to))
                .collect(Collectors.groupingBy(r -> r.getPatient().getId()));
        c.patientsMeasured = curByPatient.size();
        for (List<MedicationRecord> recs : curByPatient.values()) {
            double mean = recs.stream().mapToDouble(r -> r.getAdherencePct().doubleValue()).average().orElse(0);
            if (mean >= ADHERENCE_FLOOR) c.patientsAtOrAbove80++;
        }

        // ── Activity (system-wide period comparison) ────────────────────────────
        c.missedCur  = confirmationLogRepository.findByIsMissedTrueAndScheduledDateBetween(c.from, c.to).size();
        c.missedPrev = confirmationLogRepository.findByIsMissedTrueAndScheduledDateBetween(c.prevFrom, c.prevTo).size();
        c.visitsCur  = homeVisitRepository.countByVisitDateBetween(c.from.atStartOfDay(), c.to.plusDays(1).atStartOfDay());
        c.visitsPrev = homeVisitRepository.countByVisitDateBetween(c.prevFrom.atStartOfDay(), c.prevTo.plusDays(1).atStartOfDay());

        // ── Alerts (system-wide) ────────────────────────────────────────────────
        List<Alert> unresolved = alertRepository.findByIsResolvedFalse();
        c.unresolvedTotal    = unresolved.size();
        c.unresolvedCritical = unresolved.stream().filter(a -> a.getSeverity() == AlertSeverity.CRITICAL).count();
        c.unresolvedWarning  = unresolved.stream().filter(a -> a.getSeverity() == AlertSeverity.WARNING).count();
        c.missedDoseAlerts   = unresolved.stream().filter(a -> a.getAlertType() == AlertType.MISSED_DOSE).count();
        c.iitEscalated       = unresolved.stream().filter(a -> a.getAlertType() == AlertType.IIT_ESCALATED).count();
        c.treatmentInterrupted = unresolved.stream().filter(a -> a.getAlertType() == AlertType.TREATMENT_INTERRUPTED).count();
        c.criticalAlerts = unresolved.stream()
                .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL && a.getPatient() != null)
                .sorted(Comparator.comparing(Alert::getCreatedAt))
                .toList();

        // ── High/critical risk patients (system) ────────────────────────────────
        Map<UUID, Patient> byId = active.stream().collect(Collectors.toMap(Patient::getId, p -> p, (a, b) -> a));
        for (Map.Entry<UUID, RiskLevel> e : riskByPatient.entrySet()) {
            if (e.getValue() != RiskLevel.HIGH && e.getValue() != RiskLevel.CRITICAL) continue;
            Patient p = byId.get(e.getKey());
            if (p != null) c.highRiskPatients.add(new RiskRow(p, e.getValue()));
        }
        c.highRiskPatients.sort(Comparator.comparingInt(r -> r.level == RiskLevel.CRITICAL ? 0 : 1));

        // ── Facility league table ───────────────────────────────────────────────
        List<Alert> unresolvedWithPatient = unresolved.stream().filter(a -> a.getPatient() != null).toList();
        for (Facility f : allFacilities) {
            if (!Boolean.TRUE.equals(f.getIsActive())) continue;
            UUID fid = f.getId();
            long fPatients = active.stream().filter(p -> p.getFacility().getId().equals(fid)).count();
            long fChws = chwRepository.findByFacilityId(fid).size();
            double fAdh = med.stream().filter(r -> r.getPatient().getFacility().getId().equals(fid))
                    .mapToDouble(r -> r.getAdherencePct().doubleValue()).average().orElse(Double.NaN);
            long fHighRisk = active.stream().filter(p -> p.getFacility().getId().equals(fid))
                    .filter(p -> { RiskLevel l = riskByPatient.get(p.getId());
                        return l == RiskLevel.HIGH || l == RiskLevel.CRITICAL; }).count();
            long fAlerts = unresolvedWithPatient.stream()
                    .filter(a -> a.getPatient().getFacility().getId().equals(fid)).count();
            c.facilityRows.add(new FacilityRow(f.getName(), f.getDistrict(), fPatients, fChws, fAdh, fHighRisk, fAlerts));
            if (!Double.isNaN(fAdh) && fAdh < ADHERENCE_TARGET) c.facilitiesBelowTarget++;
        }
        // worst first: most unresolved alerts, then lowest adherence
        c.facilityRows.sort(Comparator.comparingLong((FacilityRow r) -> r.alerts).reversed()
                .thenComparingDouble(r -> Double.isNaN(r.adherence) ? 999 : r.adherence));

        return ReportModel.builder()
                .reportTitle("System-Wide Administrative Report")
                .scopeName("All Facilities")
                .subScope(c.activeFacilities + " active facilities")
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

        list.add(Indicator.builder().label("On Treatment (System)").value(String.valueOf(c.txCurr))
                .trend(Indicator.Trend.NONE).status(Indicator.Rag.NEUTRAL).target("TX_CURR").build());

        Indicator.Rag adhRag = Double.isNaN(c.adhCur) ? Indicator.Rag.NEUTRAL
                : c.adhCur >= ADHERENCE_TARGET ? Indicator.Rag.GOOD
                : c.adhCur >= ADHERENCE_FLOOR ? Indicator.Rag.WATCH : Indicator.Rag.BAD;
        list.add(Indicator.builder().label("Mean Adherence").value(pct(c.adhCur))
                .previousValue(pct(c.adhPrev)).deltaLabel(deltaPts(c.adhCur, c.adhPrev))
                .trend(trendHigherBetter(c.adhCur, c.adhPrev)).status(adhRag).target("Target >=90%").build());

        double pctAdequate = c.patientsMeasured > 0 ? 100.0 * c.patientsAtOrAbove80 / c.patientsMeasured : Double.NaN;
        list.add(Indicator.builder().label("Patients >=80% Adherent").value(pct(pctAdequate))
                .trend(Indicator.Trend.NONE)
                .status(Double.isNaN(pctAdequate) ? Indicator.Rag.NEUTRAL
                        : pctAdequate >= 85 ? Indicator.Rag.GOOD : pctAdequate >= 70 ? Indicator.Rag.WATCH : Indicator.Rag.BAD)
                .target(c.patientsMeasured + " measured").build());

        list.add(Indicator.builder().label("New Enrolments").value(String.valueOf(c.newEnrolCur))
                .previousValue(String.valueOf(c.newEnrolPrev)).deltaLabel(deltaCount(c.newEnrolCur, c.newEnrolPrev))
                .trend(trendHigherBetter(c.newEnrolCur, c.newEnrolPrev)).status(Indicator.Rag.NEUTRAL).target("TX_NEW").build());

        list.add(Indicator.builder().label("Missed Doses").value(String.valueOf(c.missedCur))
                .previousValue(String.valueOf(c.missedPrev)).deltaLabel(deltaCount(c.missedCur, c.missedPrev))
                .trend(trendHigherBetter(c.missedCur, c.missedPrev))
                .status(c.missedCur > c.missedPrev ? Indicator.Rag.WATCH : Indicator.Rag.GOOD).target("period").build());

        list.add(Indicator.builder().label("Home Visits").value(String.valueOf(c.visitsCur))
                .previousValue(String.valueOf(c.visitsPrev)).deltaLabel(deltaCount(c.visitsCur, c.visitsPrev))
                .trend(trendHigherBetter(c.visitsCur, c.visitsPrev))
                .status(c.visitsCur >= c.visitsPrev ? Indicator.Rag.GOOD : Indicator.Rag.WATCH).target("period").build());

        list.add(Indicator.builder().label("Unresolved Critical Alerts").value(String.valueOf(c.unresolvedCritical))
                .trend(Indicator.Trend.NONE)
                .status(c.unresolvedCritical > 0 ? Indicator.Rag.BAD : Indicator.Rag.GOOD).target("open now").build());

        list.add(Indicator.builder().label("FHIR Sync Failed").value(String.valueOf(c.fhirFailed))
                .trend(Indicator.Trend.NONE)
                .status(c.fhirFailed > 0 ? Indicator.Rag.BAD : Indicator.Rag.GOOD).target("data quality").build());

        return list;
    }

    private List<KvSection> buildKvSections(Ctx c) {
        List<KvSection> list = new ArrayList<>();
        list.add(KvSection.builder().title("Users & Workforce").rows(new String[][]{
                {"Total users", String.valueOf(c.totalUsers)},
                {"Active users", String.valueOf(c.activeUsers)},
                {"Community health workers", String.valueOf(c.totalChw)},
                {"Facility providers / clinical staff", String.valueOf(c.totalProviders)},
                {"Supervisors", String.valueOf(c.totalSupervisors)},
                {"Patient accounts", String.valueOf(c.totalPatientAccounts)},
        }).build());
        list.add(KvSection.builder().title("Facilities").rows(new String[][]{
                {"Total facilities", String.valueOf(c.totalFacilities)},
                {"Active facilities", String.valueOf(c.activeFacilities)},
                {"Facilities below 90% adherence", String.valueOf(c.facilitiesBelowTarget)},
        }).build());
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
        list.add(KvSection.builder().title("Alert Backlog (Unresolved)").rows(new String[][]{
                {"Total", String.valueOf(c.unresolvedTotal)},
                {"Critical", String.valueOf(c.unresolvedCritical)},
                {"Warning", String.valueOf(c.unresolvedWarning)},
                {"Missed-dose", String.valueOf(c.missedDoseAlerts)},
        }).build());
        list.add(KvSection.builder().title("Treatment Interruption (IIT)").rows(new String[][]{
                {"Escalated for tracing", String.valueOf(c.iitEscalated)},
                {"Confirmed interrupted", String.valueOf(c.treatmentInterrupted)},
        }).build());
        list.add(KvSection.builder().title("FHIR Sync Status").rows(new String[][]{
                {"Pending", String.valueOf(c.fhirPending)},
                {"Synced", String.valueOf(c.fhirSynced)},
                {"Failed", String.valueOf(c.fhirFailed)},
        }).build());
        return list;
    }

    private List<LineListing> buildLineListings(Ctx c) {
        List<LineListing> list = new ArrayList<>();

        List<String[]> facRows = new ArrayList<>();
        for (FacilityRow f : c.facilityRows) {
            facRows.add(new String[]{
                    nz(f.name), nz(f.district), String.valueOf(f.patients), String.valueOf(f.chws),
                    Double.isNaN(f.adherence) ? "-" : String.format("%.1f%%", f.adherence),
                    String.valueOf(f.highRisk), String.valueOf(f.alerts)});
        }
        list.add(LineListing.builder()
                .title("Facility League Table (Needs Attention First)")
                .headers(new String[]{"Facility", "District", "On Treatment", "CHWs", "Adherence", "High Risk", "Unresolved Alerts"})
                .rows(facRows).emptyMessage("No active facilities").build());

        List<String[]> riskRows = new ArrayList<>();
        for (RiskRow rr : capped(c.highRiskPatients)) {
            riskRows.add(new String[]{code(rr.patient), rr.level.name(), diagnosis(rr.patient),
                    rr.patient.getFacility() != null ? nz(rr.patient.getFacility().getName()) : "-"});
        }
        list.add(LineListing.builder()
                .title("High & Critical Risk Patients" + moreNote(c.highRiskPatients.size()))
                .headers(new String[]{"Patient Code", "Risk", "Diagnosis", "Facility"})
                .rows(riskRows).emptyMessage("No patients currently at high or critical risk").build());

        List<String[]> alertRows = new ArrayList<>();
        for (Alert a : capped(c.criticalAlerts)) {
            long ageDays = a.getCreatedAt() == null ? 0 : ChronoUnit.DAYS.between(a.getCreatedAt(), LocalDateTime.now());
            alertRows.add(new String[]{
                    code(a.getPatient()),
                    a.getPatient().getFacility() != null ? nz(a.getPatient().getFacility().getName()) : "-",
                    a.getAlertType() != null ? a.getAlertType().name() : "-",
                    ageDays + "d"});
        }
        list.add(LineListing.builder()
                .title("Unresolved Critical Alerts" + moreNote(c.criticalAlerts.size()))
                .headers(new String[]{"Patient Code", "Facility", "Alert Type", "Age"})
                .rows(alertRows).emptyMessage("No unresolved critical alerts").build());

        return list;
    }

    private List<Recommendation> buildRecommendations(Ctx c) {
        List<Recommendation> recs = new ArrayList<>();
        if (c.unresolvedCritical > 0) {
            recs.add(rec(Recommendation.Severity.CRITICAL,
                    c.unresolvedCritical + " critical alert(s) are unresolved across the programme.",
                    "Ensure the worst facilities (league table) triage these within 48 hours.", "Program Manager"));
        }
        if (c.fhirFailed > 0) {
            recs.add(rec(Recommendation.Severity.WARNING,
                    c.fhirFailed + " patient record(s) failed FHIR synchronisation.",
                    "Investigate sync errors and re-run the FHIR sync job.", "System Admin"));
        }
        if (!Double.isNaN(c.adhCur) && c.adhCur < ADHERENCE_TARGET) {
            String move = Double.isNaN(c.adhPrev) ? "" : " (" + deltaPts(c.adhCur, c.adhPrev) + " vs previous period)";
            recs.add(rec(Recommendation.Severity.WARNING,
                    "System adherence is " + pct(c.adhCur) + ", below the 90% target" + move + ".",
                    "Target supportive supervision at the lowest-performing facilities (league table).", "Program Manager"));
        }
        if (c.facilitiesBelowTarget > 0) {
            recs.add(rec(Recommendation.Severity.INFO,
                    c.facilitiesBelowTarget + " facility/facilities are below the 90% adherence target.",
                    "Prioritise mentorship visits to these facilities.", "Supervisor"));
        }
        if (c.treatmentInterrupted > 0 || c.iitEscalated > 0) {
            recs.add(rec(Recommendation.Severity.WARNING,
                    c.iitEscalated + " case(s) escalated for tracing and " + c.treatmentInterrupted + " confirmed interrupted.",
                    "Confirm tracing tasks are being actioned by CHW supervisors.", "CHW Supervisor"));
        }
        if (c.missedCur > 0 && c.missedCur > c.missedPrev * 1.2) {
            recs.add(rec(Recommendation.Severity.WARNING,
                    "Missed doses rose to " + c.missedCur + " this period (from " + c.missedPrev + ").",
                    "Review reminder delivery configuration system-wide.", "System Admin"));
        }
        if (recs.isEmpty()) {
            recs.add(rec(Recommendation.Severity.INFO,
                    "No critical issues detected this period.",
                    "Maintain routine monitoring across facilities.", "Program Manager"));
        }
        recs.sort(Comparator.comparingInt(r -> r.getSeverity().ordinal()));
        return recs;
    }

    private List<String> buildExecutiveSummary(Ctx c) {
        List<String> out = new ArrayList<>();

        StringBuilder what = new StringBuilder();
        what.append("Across ").append(c.activeFacilities).append(" active facilities the programme supported ")
            .append(c.txCurr).append(" patient(s) on treatment");
        if (c.newEnrolCur > 0) what.append(", with ").append(c.newEnrolCur).append(" new enrolment(s) this period");
        what.append(". ");
        if (!Double.isNaN(c.adhCur)) {
            what.append("System adherence was ").append(pct(c.adhCur));
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
            why.append("System adherence is below the 90% target, and ").append(c.facilitiesBelowTarget)
               .append(" facility/facilities are underperforming. ");
        } else {
            why.append("System adherence is at or above the 90% target. ");
        }
        why.append(c.unresolvedCritical).append(" critical alert(s) are unresolved and ")
           .append(c.fhirFailed).append(" record(s) failed FHIR sync.");
        out.add("Why it matters.||" + why);

        StringBuilder who = new StringBuilder();
        who.append(c.riskHigh + c.riskCritical).append(" patient(s) are at high or critical risk system-wide");
        if (!c.facilityRows.isEmpty()) {
            who.append("; the facilities needing attention first are listed in the league table below");
        }
        who.append(".");
        out.add("Who is affected.||" + who);

        List<Recommendation> recs = buildRecommendations(c);
        StringBuilder act = new StringBuilder();
        int n = 1;
        for (Recommendation r : recs) {
            if (r.getSeverity() == Recommendation.Severity.INFO && n > 1) break;
            act.append(n++).append(") ").append(r.getAction()).append(" (").append(r.getOwner()).append("). ");
            if (n > 3) break;
        }
        out.add("What administrators should do.||" + act.toString().trim());

        return out;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────
    private String resolveCurrentUserName() {
        try {
            String email = SecurityUtil.getCurrentUserEmail();
            return userRepository.findByEmail(email).map(SystemUser::getFullName).orElse("System Administrator");
        } catch (Exception e) {
            return "System Administrator";
        }
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
    private static long countRole(List<SystemUser> users, UserRole role) {
        return users.stream().filter(u -> u.getRole() == role).count();
    }
    private static long countSync(List<Patient> patients, SyncStatus status) {
        return patients.stream().filter(p -> p.getSyncStatus() == status).count();
    }
    private static long countRisk(Map<UUID, RiskLevel> map, RiskLevel level) {
        return map.values().stream().filter(v -> v == level).count();
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
    private static String nz(String s) { return s == null || s.isBlank() ? "-" : s; }

    // ── Holders ────────────────────────────────────────────────────────────────────
    private static final class Ctx {
        String generatedBy, periodLabel, comparisonLabel;
        LocalDate from, to, prevFrom, prevTo;
        long windowDays;
        long totalUsers, activeUsers, totalChw, totalProviders, totalSupervisors, totalPatientAccounts;
        long totalFacilities, activeFacilities, facilitiesBelowTarget;
        long txCurr, hivOnly, tbOnly, coinf, newEnrolCur, newEnrolPrev;
        long fhirPending, fhirSynced, fhirFailed;
        long riskLow, riskModerate, riskHigh, riskCritical, riskUnscored;
        double adhCur, adhPrev;
        long patientsMeasured, patientsAtOrAbove80;
        long missedCur, missedPrev, visitsCur, visitsPrev;
        long unresolvedTotal, unresolvedCritical, unresolvedWarning, missedDoseAlerts, iitEscalated, treatmentInterrupted;
        List<Alert> criticalAlerts = new ArrayList<>();
        final List<RiskRow> highRiskPatients = new ArrayList<>();
        final List<FacilityRow> facilityRows = new ArrayList<>();
    }
    private static final class RiskRow {
        final Patient patient; final RiskLevel level;
        RiskRow(Patient patient, RiskLevel level) { this.patient = patient; this.level = level; }
    }
    private static final class FacilityRow {
        final String name, district; final long patients, chws, highRisk, alerts; final double adherence;
        FacilityRow(String name, String district, long patients, long chws, double adherence, long highRisk, long alerts) {
            this.name = name; this.district = district; this.patients = patients; this.chws = chws;
            this.adherence = adherence; this.highRisk = highRisk; this.alerts = alerts;
        }
    }
}

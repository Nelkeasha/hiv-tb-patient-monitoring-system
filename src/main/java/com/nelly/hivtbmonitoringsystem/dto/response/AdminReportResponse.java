package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
public class AdminReportResponse {

    private LocalDateTime generatedAt;

    // System users
    private long totalUsers;
    private long totalChw;
    private long totalProviders;
    private long totalSupervisors;
    private long totalPatients;
    private long activeUsers;
    private long inactiveUsers;

    // Facilities
    private long totalFacilities;
    private long activeFacilities;
    private List<FacilityReportRow> facilityBreakdown;

    // Patients (system-wide)
    private long totalActivePatients;
    private long hivOnly;
    private long tbOnly;
    private long hivTbCoinfection;

    // FHIR sync status
    private long fhirSyncPending;
    private long fhirSyncSynced;
    private long fhirSyncFailed;

    // Risk distribution (system-wide)
    private long riskLow;
    private long riskModerate;
    private long riskHigh;
    private long riskCritical;
    private long riskUnscored;

    // Adherence (system-wide)
    private BigDecimal systemAdherenceAvg;
    private long belowThresholdCount;
    private long falseConfirmationFlagCount;

    // Alerts (system-wide, unresolved)
    private long unresolvedAlerts;
    private long criticalAlerts;
    private long warningAlerts;
    private long missedDoseAlerts;

    // LTFU tracing (replaces stock section)
    private long activeLtfuTasks;
    private long ltfuConfirmedCount;
    private long escalatedCount;
}

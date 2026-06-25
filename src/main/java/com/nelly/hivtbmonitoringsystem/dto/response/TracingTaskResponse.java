package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.entity.TracingTask;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder
public class TracingTaskResponse {

    private UUID id;
    private UUID patientId;
    private String patientName;
    private String patientCode;
    private String village;
    private UUID chwId;
    private String chwName;
    private LocalDate missedAppointmentDate;
    private Integer daysSinceMissed;
    private String reason;
    private String status;
    private String administrativeClassification;
    private LocalDateTime ltfuConfirmedAt;
    private String outcome;
    private String disengagementReason;
    private String resolutionPlan;
    private Boolean proxyAuthorized;
    private String proxyName;
    private String notes;
    private String escalatedToName;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public static TracingTaskResponse from(TracingTask t) {
        return TracingTaskResponse.builder()
                .id(t.getId())
                .patientId(t.getPatient().getId())
                .patientName(t.getPatient().getFullName())
                .patientCode(t.getPatient().getPatientCode())
                .village(t.getPatient().getVillage())
                .chwId(t.getChw().getId())
                .chwName(t.getChw().getUser().getFullName())
                .missedAppointmentDate(t.getMissedAppointmentDate())
                .daysSinceMissed(t.getDaysSinceMissed())
                .reason(t.getReason())
                .status(t.getStatus())
                .administrativeClassification(t.getAdministrativeClassification())
                .ltfuConfirmedAt(t.getLtfuConfirmedAt())
                .outcome(t.getOutcome())
                .disengagementReason(t.getDisengagementReason())
                .resolutionPlan(t.getResolutionPlan())
                .proxyAuthorized(t.getProxyAuthorized())
                .proxyName(t.getProxyName())
                .notes(t.getNotes())
                .escalatedToName(t.getEscalatedTo() != null ? t.getEscalatedTo().getFullName() : null)
                .createdAt(t.getCreatedAt())
                .resolvedAt(t.getResolvedAt())
                .build();
    }
}

package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class HomeVisitResponse {

    private UUID id;
    private UUID patientId;
    private String patientName;
    private String patientCode;
    private UUID chwId;
    private String chwName;
    private LocalDateTime visitDate;
    private String visitStatus;
    private String adherenceStatus;
    private Integer pillCountRecorded;
    private Integer pillCountExpected;
    private Boolean pillCountDiscrepancy;
    // ── Structured symptom screen (Gap B) ──
    private Boolean symptomCoughGe2w;
    private Boolean symptomFever;
    private Boolean symptomNightSweats;
    private Boolean symptomWeightLoss;
    private Boolean symptomHemoptysis;
    private Boolean sideEffectNeuropathy;
    private Boolean sideEffectJaundice;
    private Boolean sideEffectNausea;
    private Boolean sideEffectRash;
    private Boolean sideEffectDizziness;
    private Boolean presumptiveTb;

    private String symptomsReported;
    private String sideEffectsReported;
    private String psychosocialNotes;
    private LocalDateTime nextVisitDate;
    private Integer adverseEventGrade;
    private Boolean referralInitiated;

    // ── Differentiated DOT model (V33) ──
    private Boolean dotObserved;
    private Map<String, Boolean> tbSideEffects;
    private Map<String, Boolean> artSideEffects;
    private Boolean homeVentilationOk;
    private Boolean coughHygieneOk;
    private LocalDate nextDotDate;
    private String homeVisitTrigger;

    private Integer recordVersion;
    private String syncStatus;
    private LocalDateTime createdAt;
}

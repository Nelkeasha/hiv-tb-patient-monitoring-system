package com.nelly.hivtbmonitoringsystem.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    private String symptomsReported;
    private String sideEffectsReported;
    private String psychosocialNotes;
    private LocalDateTime nextVisitDate;
    private Integer adverseEventGrade;
    private Boolean referralInitiated;
    private Integer recordVersion;
    private String syncStatus;
    private LocalDateTime createdAt;
}

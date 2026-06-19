package com.nelly.hivtbmonitoringsystem.dto.response;

import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter @Builder
public class FacilityPatientDetailResponse {
    private UUID id;
    private String patientCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String sex;
    private String phoneNumber;
    private Boolean hasSmartphone;
    private DiagnosisType diagnosisType;
    private LocalDate artStartDate;
    private LocalDate tbTreatmentStartDate;
    private String householdLocation;
    private String village;
    private String district;
    private Boolean isActive;
    private String registrationStatus;
    private String referralId;
    private String suspectedCondition;
    private String screeningNotes;
    private java.time.LocalDateTime confirmedAt;
    private String chwName;
    private AiRiskScoreResponse latestRiskScore;
    private List<AlertResponse> unresolvedAlerts;
    private List<HomeVisitResponse> recentHomeVisits;
}

package com.nelly.hivtbmonitoringsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class CompleteSyncRequest {

    /** "COMPLETED", "PARTIAL_FAILURE", or "FAILED" */
    @NotBlank
    private String syncStatus;

    private int recordsSynced;
    private int recordsFailed;
    private String errorLog;

    /** patientId (UUID string) → FHIR Patient resource ID */
    private Map<String, String> patientFhirIds;

    /** homeVisitId (UUID string) → FHIR Observation resource ID */
    private Map<String, String> homeVisitFhirIds;

    /** medicationRecordId (Long as string) → FHIR MedicationStatement resource ID */
    private Map<String, String> medicationRecordFhirIds;

    /** treatmentPlanId (UUID string) → FHIR CarePlan resource ID */
    private Map<String, String> treatmentPlanFhirIds;
}

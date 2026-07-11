package com.nelly.hivtbmonitoringsystem.enums;

/**
 * Alert types as defined in the thesis data dictionary (Table 18).
 * IIT_ESCALATED and TREATMENT_INTERRUPTED added per Update 2 (renamed
 * from LTFU_TRACING/LTFU_CONFIRMED in V25 to align with PEPFAR/IIT
 * terminology used in the operational tracing workflow).
 * LOW_ADHERENCE, LOW_STOCK, MISSED_VISIT removed per Update 1.
 */
public enum AlertType {
    MISSED_DOSE,
    FALSE_CONFIRMATION,
    CLINICAL_DISCREPANCY,
    EARLY_WARNING,
    IIT_ESCALATED,
    TREATMENT_INTERRUPTED,
    LTFU_TRACING_RESOLVED,
    SYNC_FAILURE,
    NEW_PATIENT_ASSIGNMENT,

    /** Fired when a CHW records a home visit with a CTCAE Grade 3/4 adverse_event_grade (V27). */
    ADVERSE_EVENT,

    /**
     * In-app notification to the screening CHW when clinical staff confirm
     * their provisional referral (PROVISIONAL → CONFIRMED, fires once). Email
     * and SMS are disabled in this deployment, so this is the reliable
     * channel alongside the FCM push (V40).
     */
    REFERRAL_CONFIRMED,

    /**
     * Produced by the Python AI service's clinical_correlation_service
     * (Pattern E) — fired when two consecutive HIV viral load readings polled
     * from FHIR Observations both exceed 50 copies/mL. Written directly to
     * the alerts table from Python as the raw string "TREATMENT_FAILURE_RISK"
     * (alert_utils.create_alert has no enum dependency on this class), so
     * this value exists purely to keep the Java-side AlertType enum a
     * complete mirror of every alert_type the system actually produces.
     */
    TREATMENT_FAILURE_RISK
}

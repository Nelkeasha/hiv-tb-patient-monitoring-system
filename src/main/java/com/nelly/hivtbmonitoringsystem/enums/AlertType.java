package com.nelly.hivtbmonitoringsystem.enums;

/**
 * Alert types as defined in the thesis data dictionary (Table 18).
 * LTFU_TRACING and LTFU_CONFIRMED added per Update 2.
 * LOW_ADHERENCE, LOW_STOCK, MISSED_VISIT removed per Update 1.
 */
public enum AlertType {
    MISSED_DOSE,
    FALSE_CONFIRMATION,
    CLINICAL_DISCREPANCY,
    EARLY_WARNING,
    LTFU_TRACING,
    LTFU_CONFIRMED,
    LTFU_TRACING_RESOLVED,
    SYNC_FAILURE,
    NEW_PATIENT_ASSIGNMENT
}

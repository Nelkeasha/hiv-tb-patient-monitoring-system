package com.nelly.hivtbmonitoringsystem.validation;

/**
 * Single source of truth for every validation error message in the
 * backend. Bean Validation annotation {@code message} attributes must be
 * compile-time constants, so every DTO field references one of these
 * constants instead of writing its own wording — this is what keeps "Phone
 * number must be 10 digits starting with 07" worded identically wherever
 * a phone number is collected, instead of each form inventing its own
 * (and often vaguer) text.
 */
public final class ValidationMessages {

    private ValidationMessages() {}

    // ── Generic, reusable across many fields ──────────────────────────────
    public static final String FULL_NAME_REQUIRED = "Full name is required";
    public static final String FULL_NAME_TOO_LONG = "Full name must be at most 100 characters";
    public static final String DATE_OF_BIRTH_REQUIRED = "Date of birth is required";
    public static final String DATE_OF_BIRTH_NOT_FUTURE = "Date of birth cannot be in the future";
    public static final String EMAIL_REQUIRED = "Email address is required";
    public static final String EMAIL_INVALID = "Please enter a valid email address (e.g. name@example.com)";
    public static final String PHONE_REQUIRED = "Phone number is required";
    public static final String PHONE_INVALID = "Phone number must be 10 digits starting with 07 (e.g. 0788123456), or start with +250";
    public static final String NATIONAL_ID_INVALID = "National ID must be exactly 16 digits";
    public static final String SEX_INVALID = "Sex must be MALE, FEMALE, OTHER, or UNKNOWN";
    public static final String FACILITY_REQUIRED = "Please select a facility";
    public static final String DISTRICT_REQUIRED = "District is required";
    public static final String EMPLOYEE_CODE_REQUIRED = "Employee code is required";
    public static final String EMPLOYEE_CODE_INVALID = "Employee code may only contain letters, numbers, and hyphens (3-50 characters)";
    public static final String VILLAGE_REQUIRED = "Assigned village is required";
    public static final String SECTOR_REQUIRED = "Assigned sector is required";

    // ── Patient registration / screening ──────────────────────────────────
    public static final String DIAGNOSIS_TYPE_REQUIRED = "Please select a diagnosis type (HIV, TB, or HIV/TB co-infection)";
    public static final String SUSPECTED_CONDITION_INVALID = "Suspected condition must be TB, HIV, or HIV_TB_COINFECTION";
    public static final String HOUSEHOLD_LOCATION_TOO_LONG = "Household location must be at most 255 characters";
    public static final String VILLAGE_TOO_LONG = "Village must be at most 100 characters";
    public static final String SECTOR_TOO_LONG = "Sector must be at most 100 characters";
    public static final String DISTRICT_TOO_LONG = "District must be at most 100 characters";

    // ── Clinical confirmations / dose schedules ───────────────────────────
    public static final String SCHEDULE_ID_REQUIRED = "Please select a dose schedule to confirm";
    public static final String DOSE_TIME_REQUIRED = "Dose time is required (e.g. 08:00)";
    public static final String WINDOW_DURATION_TOO_SHORT = "Confirmation window must be at least 1 minute";
    public static final String WINDOW_DURATION_TOO_LONG = "Confirmation window cannot exceed 24 hours (1440 minutes)";

    // ── Home visits ────────────────────────────────────────────────────────
    public static final String PATIENT_ID_REQUIRED = "Please select a patient";
    public static final String VISIT_DATE_REQUIRED = "Visit date is required";
    public static final String VISIT_DATE_NOT_FUTURE = "Visit date cannot be in the future";
    public static final String ADHERENCE_STATUS_REQUIRED = "Adherence status is required";
    public static final String ADHERENCE_STATUS_INVALID = "Adherence status must be GOOD, PARTIAL, POOR, or MISSED";
    public static final String PILL_COUNT_NEGATIVE = "Pill count cannot be negative";

    // ── Referrals ──────────────────────────────────────────────────────────
    public static final String REFERRAL_REASON_REQUIRED = "Please describe the reason for this referral";
    public static final String REFERRAL_URGENCY_REQUIRED = "Please select an urgency level (LOW, MEDIUM, HIGH, or EMERGENCY)";

    // ── Treatment plans / medication ──────────────────────────────────────
    public static final String MEDICATION_NAME_REQUIRED = "Medication name is required";
    public static final String DOSAGE_REQUIRED = "Dosage is required (e.g. 1 tablet)";
    public static final String FREQUENCY_REQUIRED = "Frequency is required (e.g. once daily)";
    public static final String START_DATE_REQUIRED = "Start date is required";
    public static final String END_DATE_BEFORE_START = "End date cannot be before the start date";

    // ── AI risk scoring ────────────────────────────────────────────────────
    public static final String RISK_LEVEL_REQUIRED = "Please select a risk level (LOW, MODERATE, HIGH, or CRITICAL)";
    public static final String RISK_SCORE_REQUIRED = "Risk score is required";
    public static final String RISK_SCORE_RANGE = "Risk score must be between 0 and 100";

    // ── Generic factory helpers for cases that need a dynamic field name ──
    public static String required(String fieldLabel) {
        return fieldLabel + " is required";
    }

    public static String maxLength(String fieldLabel, int max) {
        return fieldLabel + " must be at most " + max + " characters";
    }

    public static String alreadyExists(String fieldLabel, String value) {
        return "A record with this " + fieldLabel + " (" + value + ") already exists";
    }

    public static String invalidTransition(String entity, String from, String to) {
        return "Cannot move " + entity + " from " + from + " to " + to + " — that change isn't allowed";
    }
}

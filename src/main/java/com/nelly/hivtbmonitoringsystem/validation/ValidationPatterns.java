package com.nelly.hivtbmonitoringsystem.validation;

/**
 * Single source of truth for every input-format regex used across the
 * backend. The web (lib/validation/rules.ts) and mobile
 * (lib/core/validation/validators.dart) validators mirror these exact
 * patterns so the same input is accepted/rejected identically everywhere —
 * keep all three in sync if a rule changes here.
 */
public final class ValidationPatterns {

    private ValidationPatterns() {}

    /** Rwanda mobile number: 10 digits starting with 07, or +250 followed by 7 and 8 digits. */
    public static final String RWANDA_PHONE = "^(07\\d{8}|\\+2507\\d{8})$";

    /** Rwanda national ID: exactly 16 digits. */
    public static final String RWANDA_NATIONAL_ID = "^\\d{16}$";

    public static final String SEX = "MALE|FEMALE|OTHER|UNKNOWN";

    /** Used where sex must be definitively known (e.g. confirmed clinical registration) — no UNKNOWN option. */
    public static final String SEX_DEFINITIVE = "MALE|FEMALE|OTHER";

    public static final String DIAGNOSIS_SUSPECTED_CONDITION = "TB|HIV|HIV_TB_COINFECTION";

    public static final String ADHERENCE_STATUS = "GOOD|PARTIAL|POOR|MISSED";

    /** Employee code: letters, digits, and hyphens only (e.g. CHW-001). */
    public static final String EMPLOYEE_CODE = "^[A-Za-z0-9-]{3,50}$";

    /** Geohash: standard base32 alphabet (no a/i/l/o), 1-12 chars. The app only ever sends precision-7 (~150m) hashes — never raw coordinates. */
    public static final String GEOHASH = "^[0-9b-hjkmnp-z]{1,12}$";
}

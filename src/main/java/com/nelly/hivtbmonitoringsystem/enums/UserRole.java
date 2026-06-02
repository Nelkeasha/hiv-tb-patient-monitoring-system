package com.nelly.hivtbmonitoringsystem.enums;

public enum UserRole {
    PATIENT,
    CHW,
    FACILITY_PROVIDER,   // legacy — maps to CLINICAL_STAFF in thesis
    SUPERVISOR,
    SYSTEM_ADMIN,        // legacy — maps to ADMIN in thesis
    CLINICAL_STAFF,      // new — web dashboard role per thesis Table 11
    ADMIN                // new — everywhere access per thesis Table 11
}

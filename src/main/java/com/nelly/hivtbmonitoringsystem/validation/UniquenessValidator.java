package com.nelly.hivtbmonitoringsystem.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Centralizes the "a record with this value already exists" checks that
 * used to be duplicated, with inconsistent wording, across
 * {@code PatientService}, {@code UserManagementService}, etc. The
 * repository's {@code existsBy...} call still has to happen at the call
 * site (no generic way to express that without reflection), but the
 * decision to throw — and the exact wording — lives in one place.
 */
@Component
public class UniquenessValidator {

    /**
     * @param field      DTO field name, so the frontend can attach the error inline
     * @param fieldLabel human-readable label, e.g. "National ID"
     * @param value      the offending value, echoed back in the message
     * @param alreadyExists  result of the caller's {@code repository.existsBy...(value)} check
     */
    public void ensureUnique(String field, String fieldLabel, String value, boolean alreadyExists) {
        ensureUnique(field, alreadyExists, ValidationMessages.alreadyExists(fieldLabel, value));
    }

    /** Use when the generic "already exists" wording isn't specific enough (e.g. "already linked to another account"). */
    public void ensureUnique(String field, boolean alreadyExists, String message) {
        if (alreadyExists) {
            throw new BusinessRuleException(field, message, HttpStatus.CONFLICT);
        }
    }
}

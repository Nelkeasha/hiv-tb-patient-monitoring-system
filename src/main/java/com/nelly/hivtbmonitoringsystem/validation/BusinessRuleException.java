package com.nelly.hivtbmonitoringsystem.validation;

import org.springframework.http.HttpStatus;

/**
 * Thrown by the centralized business-rule validators (uniqueness, status
 * transitions, role/ownership checks) instead of each service inventing its
 * own ad hoc {@code ResponseStatusException}. Carries an optional
 * {@code field} so the frontend can attach the message to the right form
 * field, exactly like a Bean Validation field error — from the API
 * consumer's point of view, "this national ID is already registered" and
 * "this field must not be blank" should look like the same kind of error.
 */
public class BusinessRuleException extends RuntimeException {

    private final String field;
    private final HttpStatus status;

    public BusinessRuleException(String message, HttpStatus status) {
        this(null, message, status);
    }

    public BusinessRuleException(String field, String message, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
    }

    public String getField() {
        return field;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

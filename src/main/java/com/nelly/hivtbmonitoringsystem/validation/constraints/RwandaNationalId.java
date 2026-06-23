package com.nelly.hivtbmonitoringsystem.validation.constraints;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates a Rwanda national ID (exactly 16 digits). Null/blank values
 * pass — national ID is optional on most patient records (e.g. provisional
 * screenings before lab confirmation), so pair with {@code @NotBlank}
 * wherever it's actually mandatory.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RwandaNationalIdValidator.class)
public @interface RwandaNationalId {
    String message() default ValidationMessages.NATIONAL_ID_INVALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

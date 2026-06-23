package com.nelly.hivtbmonitoringsystem.validation.constraints;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates a Rwanda mobile phone number (10 digits starting with 07, or
 * +250 followed by 7 and 8 digits). Null/blank values pass — pair with
 * {@code @NotBlank} on fields where a phone number is mandatory.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RwandaPhoneValidator.class)
public @interface RwandaPhone {
    String message() default ValidationMessages.PHONE_INVALID;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

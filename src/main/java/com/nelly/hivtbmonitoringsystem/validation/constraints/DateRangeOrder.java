package com.nelly.hivtbmonitoringsystem.validation.constraints;

import com.nelly.hivtbmonitoringsystem.validation.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint: the field named {@code end} must not be before the
 * field named {@code start} (both {@code LocalDate}). Skips the check if
 * either field is null — pair with {@code @NotNull} on whichever field is
 * actually mandatory. One reusable annotation instead of every DTO with a
 * date range writing its own cross-field check.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeOrderValidator.class)
public @interface DateRangeOrder {
    String start();
    String end();
    String message() default ValidationMessages.END_DATE_BEFORE_START;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

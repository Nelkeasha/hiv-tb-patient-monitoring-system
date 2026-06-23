package com.nelly.hivtbmonitoringsystem.validation.constraints;

import com.nelly.hivtbmonitoringsystem.validation.ValidationPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class RwandaPhoneValidator implements ConstraintValidator<RwandaPhone, String> {

    private static final Pattern PATTERN = Pattern.compile(ValidationPatterns.RWANDA_PHONE);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // pair with @NotBlank for required fields
        return PATTERN.matcher(value.trim()).matches();
    }
}

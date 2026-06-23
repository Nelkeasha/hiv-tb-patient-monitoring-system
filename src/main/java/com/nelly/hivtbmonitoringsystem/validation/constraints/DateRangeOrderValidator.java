package com.nelly.hivtbmonitoringsystem.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class DateRangeOrderValidator implements ConstraintValidator<DateRangeOrder, Object> {

    private String start;
    private String end;

    @Override
    public void initialize(DateRangeOrder annotation) {
        this.start = annotation.start();
        this.end = annotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
        Object startVal = wrapper.getPropertyValue(start);
        Object endVal = wrapper.getPropertyValue(end);
        if (!(startVal instanceof LocalDate startDate) || !(endVal instanceof LocalDate endDate)) return true;
        return !endDate.isBefore(startDate);
    }
}

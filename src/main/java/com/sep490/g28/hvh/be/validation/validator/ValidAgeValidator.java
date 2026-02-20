package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.ValidAge;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

public class ValidAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {
    private int min;
    private int max;
    private Clock clock = Clock.systemDefaultZone(); //todo: cho nay can xem lai set sytem default zone

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        // Allow null value. Use @NotNull if required.
        if (dob == null) {
            return true;
        }

        LocalDate today = LocalDate.now(clock);

        // Date of birth must be in the past
        if (!dob.isBefore(today)) {
            return false;
        }

        int age = Period.between(dob, today).getYears();

        return age >= min && age <= max;
    }
}

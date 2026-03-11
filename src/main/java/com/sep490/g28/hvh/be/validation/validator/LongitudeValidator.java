package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.ValidLongitude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LongitudeValidator implements ConstraintValidator<ValidLongitude, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true; // not check null
        return value>= -180 && value<= 180;
    }
}

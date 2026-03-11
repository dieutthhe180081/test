package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.ValidLatitude;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LatitudeValidator implements ConstraintValidator<ValidLatitude, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true; // not check null
        return value>= -90 && value<= 90;
    }
}

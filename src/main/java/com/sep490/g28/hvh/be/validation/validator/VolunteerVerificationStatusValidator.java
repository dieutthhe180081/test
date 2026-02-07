package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.validation.VolunteerVerificationStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class VolunteerVerificationStatusValidator implements ConstraintValidator<VolunteerVerificationStatus, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isBlank()) return true;

        // Ensure the payment type exists within the enum
        try {
            EVolunteerVerificationStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}

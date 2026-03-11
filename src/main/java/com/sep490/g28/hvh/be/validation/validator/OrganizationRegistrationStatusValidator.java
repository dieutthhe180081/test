package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.constant.EOrgRegistrationStatus;
import com.sep490.g28.hvh.be.validation.OrganizationRegistrationStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrganizationRegistrationStatusValidator implements ConstraintValidator<OrganizationRegistrationStatus, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isBlank()) return true;

        // Ensure the payment type exists within the enum
        try {
            EOrgRegistrationStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}

package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.constant.EOrgType;
import com.sep490.g28.hvh.be.validation.OrgType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class OrgTypeValidator implements ConstraintValidator<OrgType, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        // Ensure the payment type exists within the enum
        try {
            EOrgType.valueOf(s);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}

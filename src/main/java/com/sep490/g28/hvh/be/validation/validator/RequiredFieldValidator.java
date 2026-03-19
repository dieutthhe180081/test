package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.RequiredField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public class RequiredFieldValidator implements ConstraintValidator<RequiredField, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null) return false;

        if (o instanceof String s) {
            return !s.isBlank();
        }

        if (o instanceof Collection<?> c) {
            return !c.isEmpty();
        }

        return true;
    }
}

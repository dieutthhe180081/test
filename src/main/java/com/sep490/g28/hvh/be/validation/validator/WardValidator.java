package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.dictionary.WardDictionary;
import com.sep490.g28.hvh.be.validation.ValidWard;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WardValidator implements ConstraintValidator<ValidWard, String> {

    private final WardDictionary wardDictionary;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // not check null
        return wardDictionary.contains(value);
    }
}

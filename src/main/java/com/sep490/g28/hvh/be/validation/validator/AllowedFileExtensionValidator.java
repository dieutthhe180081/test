package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.AllowedFileExtension;
import com.sep490.g28.hvh.be.validation.ImageFileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class AllowedFileExtensionValidator implements ConstraintValidator<AllowedFileExtension, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Set<String> validExtensions = Set.of(".jpeg", ".jpg", ".png", ".pdf");
        if (s != null) {
            String[] words = s.split("\\s+");
            for (String w : words) {
                if (!validExtensions.contains(w)) {
                    return false;
                }

            }
        }
        return true;
    }
}

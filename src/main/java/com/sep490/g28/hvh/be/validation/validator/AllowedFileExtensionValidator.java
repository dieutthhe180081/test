package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.AllowedFileExtension;
import com.sep490.g28.hvh.be.validation.ImageFileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;

@Slf4j
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

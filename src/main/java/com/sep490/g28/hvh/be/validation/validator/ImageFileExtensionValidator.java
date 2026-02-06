package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.ImageFileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFileExtensionValidator implements ConstraintValidator<ImageFileExtension, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.equals(".jpeg") || s.equals(".jpg") || s.equals(".png");
    }
}

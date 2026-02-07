package com.sep490.g28.hvh.be.validation.validator;

import com.sep490.g28.hvh.be.validation.AllowedFileExtension;
import com.sep490.g28.hvh.be.validation.ImageFileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedFileExtensionValidator implements ConstraintValidator<AllowedFileExtension, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null){
            return s.equals(".jpeg") || s.equals(".jpg") || s.equals(".png") || s.equals(".pdf");
        }
        return true;
    }
}

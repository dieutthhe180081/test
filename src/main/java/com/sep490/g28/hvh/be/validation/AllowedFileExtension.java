package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.AllowedFileExtensionValidator;
import com.sep490.g28.hvh.be.validation.validator.ImageFileExtensionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedFileExtensionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedFileExtension {
    String message() default "INVALID_FILE_TYPE";
    Class<?>[] groups() default {};
    String fieldName() default "This field";
    Class<? extends Payload>[] payload() default {};
}

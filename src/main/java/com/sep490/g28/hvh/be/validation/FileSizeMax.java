package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.FileSizeMaxValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileSizeMaxValidator.class})
public @interface FileSizeMax {
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String message() default "INVALID_FILE_SIZE_MAX";
    int maxFileSizeMb() default 5;
    String fieldName() default "This field";
}

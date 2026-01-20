package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.ImageMimeTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageMimeTypeValidator.class})
public @interface ImageMimeType {
    String message() default "INVALID_IMAGE_TYPE";

    String fieldName() default "This field";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

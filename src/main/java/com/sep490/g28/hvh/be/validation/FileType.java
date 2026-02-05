package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.FileTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileTypeValidator.class})
public @interface FileType {

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String message() default "INVALID_FILE_TYPE";
    String fieldName() default "This field";
    String[] allowedTypes(); //eg: {"image/jpeg", "image/png"}
    String allowedTypesMessage() default "";
}

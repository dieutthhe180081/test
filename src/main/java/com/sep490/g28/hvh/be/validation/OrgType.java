package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.OrgTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OrgTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrgType {
    String message() default "INVALID_ORG_TYPE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

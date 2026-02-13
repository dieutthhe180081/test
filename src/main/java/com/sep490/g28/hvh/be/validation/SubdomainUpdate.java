package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.SubdomainUpdateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SubdomainUpdateValidator.class)
public @interface SubdomainUpdate {
    String message() default "INVALID_ORG_TYPE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

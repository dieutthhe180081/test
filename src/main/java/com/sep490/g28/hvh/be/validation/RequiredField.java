package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.RequiredFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to verify that a field has information (not null and not blank, not empty with String)
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @RequiredField(fieldName = "Tên tổ chức")
 * String organizationName;
 * }
 * </pre>
 *
 * <p>The {@code fieldName} attribute is used to customize validation
 * error messages, making them more user-friendly.</p>
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredFieldValidator.class)
public @interface RequiredField {

    String message() default "MISSING_REQUIRED_FIELD";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String fieldName() default  "This field";
}

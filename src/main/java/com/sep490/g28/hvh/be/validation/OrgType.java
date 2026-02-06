package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.constant.EOrgType;
import com.sep490.g28.hvh.be.validation.validator.OrgTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to verify that a field contains
 * a valid **organization type**.
 *
 * <p>This annotation validates that the value is same as one of value in {@link EOrgType}.</p>
 *
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @OrgType
 * String orgType;
 * }
 * </pre>
 *
 * <p>The {@code fieldName} attribute is used to customize validation
 * error messages, making them more user-friendly.</p>
 *
 * <p>This annotation is part of the public API contract and will be
 * included in generated Javadoc.</p>
 */
@Documented
@Constraint(validatedBy = OrgTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrgType {
    String message() default "INVALID_ORG_TYPE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

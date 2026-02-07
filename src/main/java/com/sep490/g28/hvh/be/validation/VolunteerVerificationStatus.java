package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.constant.EVolunteerVerificationStatus;
import com.sep490.g28.hvh.be.validation.validator.VolunteerVerificationStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to verify that a field contains
 * a valid **VolunteerVerificationStatus type**.
 *
 * <p>This annotation validates that the value is same as one of value in {@link EVolunteerVerificationStatus}.</p>
 *
 * <p><b>Null handling:</b><br>
 * This annotation <b>allows {@code null}</b> values by design.
 * If the field is required and must not be {@code null} or blank,
 * it must be combined with {@link jakarta.validation.constraints.NotBlank}
 * or {@link jakarta.validation.constraints.NotNull}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @VolunteerVerificationStatus
 * String status;
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
@Constraint(validatedBy = VolunteerVerificationStatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface VolunteerVerificationStatus {
    String message() default "INVALID_VOLUNTEER_VERIFICATION_STATUS";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

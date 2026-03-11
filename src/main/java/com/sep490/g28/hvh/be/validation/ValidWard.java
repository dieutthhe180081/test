package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.WardValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to validate a String as a ward's name
 *
 *
 * <p><b>Null handling:</b><br>
 * This annotation <b>allows {@code null}</b> values by design.
 * If the field is mandatory, it must be combined with
 * {@link jakarta.validation.constraints.NotNull}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @ValidWard
 * private String address;
 * }
 * </pre>
 *
 * <p>The {@code min} and {@code max} attributes define the allowed
 * age range. The {@code message} attribute can be used to customize
 * the validation error message.</p>
 */
@Documented
@Constraint(validatedBy = WardValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWard {
    String message() default "INVALID_ADDRESS"; // Default error message
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

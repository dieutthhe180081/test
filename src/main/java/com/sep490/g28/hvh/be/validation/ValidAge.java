package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.AllowedFileExtensionValidator;
import com.sep490.g28.hvh.be.validation.validator.ValidAgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to validate a date of birth against
 * a configurable age range.
 *
 * <p>This constraint checks whether the age calculated from the given
 * date of birth is between the specified {@code min} and {@code max}
 * values (inclusive).</p>
 *
 * <p><b>Default min age = 18 and max age = 80:</b></p>
 *
 * <p><b>Null handling:</b><br>
 * This annotation <b>allows {@code null}</b> values by design.
 * If the field is mandatory, it must be combined with
 * {@link jakarta.validation.constraints.NotNull}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @ValidAge(min = 18, max = 100)
 * LocalDate dob;
 * }
 * </pre>
 *
 * <p>The {@code min} and {@code max} attributes define the allowed
 * age range. The {@code message} attribute can be used to customize
 * the validation error message.</p>
 */
@Documented
@Constraint(validatedBy = ValidAgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "INVALID_DATE_OF_BIRTH"; // Default error message
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min() default 18;
    int max() default 80;
}

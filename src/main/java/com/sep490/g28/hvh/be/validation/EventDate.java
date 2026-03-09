package com.sep490.g28.hvh.be.validation;


import com.sep490.g28.hvh.be.validation.validator.EventDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to verify that input data about event date is valid
 *
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
 * @EventDate
 * public class EditEventRequest{}
 * }
 * </pre>
 */
@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target({ ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDate {
    String message() default "INVALID_EVENT_DATE_TIME";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

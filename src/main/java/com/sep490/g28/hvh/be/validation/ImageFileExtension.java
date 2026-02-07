package com.sep490.g28.hvh.be.validation;

import com.sep490.g28.hvh.be.validation.validator.ImageFileExtensionValidator;
import com.sep490.g28.hvh.be.validation.validator.OrgTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation used to verify that a field contains
 * a valid **image file extension**.
 *
 * <p>This annotation validates that the value represents an allowed
 * image file extension such as {@code .jpg}, {@code .jpeg}, or {@code .png}.</p>
 *
 * <p>It is intended for fields that store only the file extension,
 * not the full filename or MIME type.</p>
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
 * @ImageFileExtension(fieldName = "Holding ID card image")
 * String cidHoldingFileExtension;
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
@Constraint(validatedBy = ImageFileExtensionValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageFileExtension {
    String message() default "INVALID_IMAGE_TYPE";
    Class<?>[] groups() default {};
    String fieldName() default "This field";
    Class<? extends Payload>[] payload() default {};
}

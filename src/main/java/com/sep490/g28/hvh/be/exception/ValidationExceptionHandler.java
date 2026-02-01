package com.sep490.g28.hvh.be.exception;

import com.sep490.g28.hvh.be.dto.ExceptionResponse;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.ValidationErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles validation-related exceptions with highest precedence.
 *
 * <p>Centrally processes all validation errors coming from:
 * <ul>
 *   <li>DTO field validation (@Valid, @Validated)</li>
 *   <li>Method-level validation (request params, path variables, service methods)</li>
 *   <li>Request body parsing and type mismatches</li>
 * </ul>
 *
 * <p>Returns standardized validation error responses.</p>
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    /**
     * Supported validation annotation attributes
     * used for message placeholder replacement.
     */
    private static final Set<String> VALIDATORS_ATTRIBUTES  = Set.of(
            "fieldName",
            "max",
            "min",
            "maxFileSizeMb",
            "allowedTypesMessage"
    );

    /**
     * Handles DTO field and object validation errors.
     *
     * <p>Triggered when {@code @Valid} / {@code @Validated}
     * fails on {@code @RequestBody}.</p>
     *
     * @param ex validation exception
     * @return response containing field-level error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("Exception is catch by handleValidationException");
        log.info(ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        //get the error from the validation error to create error message
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    String field = error.getField();
                            try {
                                ValidationErrorCode errorCode = ValidationErrorCode.valueOf(error.getDefaultMessage());

                                //get the attribute of Validation Annotation
                                @SuppressWarnings("unchecked")
                                Map<String, Object> attributes =
                                        error.unwrap(ConstraintViolation.class)
                                                .getConstraintDescriptor()
                                                .getAttributes();

                                String message = formatMessage(errorCode, attributes);
                                errors.put(field, message);
                            } catch (IllegalArgumentException exception) { //the error code not existed
                                log.error("Invalid error code in validation annotation, check spelling mistake: {}", exception.getMessage());
                                errors.put(field, "Invalid data input (CHECK SPELLING IN VALIDATION ANNOTATION)");
                            }
                        }
                );

        return ResponseEntity.badRequest().body(new ExceptionResponse<>(4000, "Validation error", errors));
    }

    /**
     * Handles method-level constraint violations.
     *
     * <p>Applies to validation on request parameters,
     * path variables, and service-layer method parameters.</p>
     *
     * @param ex constraint violation exception
     * @return response containing validation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.info("Exception is caught by handleConstraintViolationException");
        log.info(ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            // get field từ propertyPath (vd: "registerAccountRequest.email")
            String field = violation.getPropertyPath().toString();
            if (field.contains(".")) {
                field = field.substring(field.lastIndexOf('.') + 1); //get the last one
            }
            try {
                // get ErrorCode name from exception message
                ValidationErrorCode errorCode = ValidationErrorCode.valueOf(violation.getMessage());

                Map<String, Object> attributes =
                        violation.getConstraintDescriptor().getAttributes();

                String message = formatMessage(errorCode, attributes);
                errors.put(field, message);

            } catch (IllegalArgumentException exception) { // error code không tồn tại
                log.error("Invalid error code");
                errors.put(field, "Invalid data input");
            }
        });

        return ResponseEntity.badRequest().body(new ExceptionResponse<>(4000, "Validation error", errors));
    }

    /**
     * Replaces placeholders in validation messages.
     *
     * @param errorCode validation error code
     * @param attributes validation annotation attributes
     * @return formatted error message
     */
    private String formatMessage(
            ValidationErrorCode errorCode,
            Map<String, Object> attributes
    ) {
        String message = errorCode.getMessage();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (VALIDATORS_ATTRIBUTES.contains(key)) {
                //the attributes exist in the validation attribute
                String value = String.valueOf(entry.getValue());
                //replace the value of the attribute to the error message
                message = message.replace("{" + key + "}", value);
            }
        }
        return message;
    }

    /**
     * Handles type mismatch errors for request parameters and path variables.
     *
     * @param e type mismatch exception
     * @return standardized error response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.info("Exception is caught by handleTypeMismatchException");
        log.warn("Type mismatch: {}", e.getMessage());
        var response = new ExceptionResponse<String>();
        response.setCode(ValidationErrorCode.INVALID_DATA_TYPE.getCode());
        response.setMessage(ValidationErrorCode.INVALID_DATA_TYPE.getMessage() + e.getName());
        return ResponseEntity.status(ValidationErrorCode.INVALID_DATA_TYPE.getHttpStatus()).body(response);
    }

    /**
     * Handles invalid or unreadable request bodies.
     *
     * <p>Commonly occurs during JSON-to-DTO mapping.</p>
     *
     * @param e message not readable exception
     * @return standardized error response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.info("Exception is caught by handleHttpMessageNotReadableException");
        log.warn("Invalid request body: {}", e.getMessage());
        var response = new ExceptionResponse<String>();
        response.setCode(ValidationErrorCode.INVALID_REQUEST_FORMAT.getCode());
        response.setMessage(ValidationErrorCode.INVALID_REQUEST_FORMAT.getMessage());
        return ResponseEntity.status(ValidationErrorCode.INVALID_REQUEST_FORMAT.getHttpStatus()).body(response);
    }

    /**
     * Handles missing required query parameters.
     *
     * @param e missing request parameter exception
     * @return standardized error response
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse<String>> handleMissingRequestParam(
            MissingServletRequestParameterException e
    ) {
        log.info("Exception is caught by handleMissingRequestParam");
        log.warn("Missing request parameter: {}", e.getMessage());

        var response = new ExceptionResponse<String>();
        response.setCode(ValidationErrorCode.MISSING_QUERY_PARAM.getCode());
        response.setMessage(
                ValidationErrorCode.MISSING_QUERY_PARAM.getMessage()
                        + e.getParameterName()
        );
        return ResponseEntity
                .status(ValidationErrorCode.MISSING_QUERY_PARAM.getHttpStatus())
                .body(response);
    }

}

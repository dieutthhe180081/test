package com.sep490.g28.hvh.be.exception;

import com.sep490.g28.hvh.be.dto.ExceptionResponse;
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

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    private static final Set<String> VALIDATORS_ATTRIBUTES  = Set.of(
            "fieldName",
            "max",
            "min",
            "maxFileSizeMb",
            "allowedTypesMessage"
    );

    /**
     * for field validation (DTO's field), object validation (bean validation on DTO)
     * for using @Valid @Validate before  @RequestBody
     *
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
     * for fail validation, method-level validation
     * validate method's parameters (request param, path variable, service layer)
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
     * use to replace the placeholder in error message ({})
     * @param errorCode the ValidationErrorCode name
     * @param attributes the attributes of the validation annotation
     * @return a String as final message
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

    //  for query param, path variable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.info("Exception is caught by handleTypeMismatchException");
        log.warn("Type mismatch: {}", e.getMessage());
        var response = new ExceptionResponse<String>();
        response.setCode(ValidationErrorCode.INVALID_DATA_TYPE.getCode());
        response.setMessage(ValidationErrorCode.INVALID_DATA_TYPE.getMessage() + e.getName());
        return ResponseEntity.status(ValidationErrorCode.INVALID_DATA_TYPE.getHttpStatus()).body(response);
    }

    //    usually ussing mapping from JSON to DTO
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.info("Exception is caught by handleHttpMessageNotReadableException");
        log.warn("Invalid request body: {}", e.getMessage());
        var response = new ExceptionResponse<String>();
        response.setCode(ValidationErrorCode.INVALID_REQUEST_FORMAT.getCode());
        response.setMessage(ValidationErrorCode.INVALID_REQUEST_FORMAT.getMessage());
        return ResponseEntity.status(ValidationErrorCode.INVALID_REQUEST_FORMAT.getHttpStatus()).body(response);
    }

    //missing query param/ @request param
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

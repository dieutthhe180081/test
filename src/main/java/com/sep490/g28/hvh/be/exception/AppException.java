package com.sep490.g28.hvh.be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base application exception.
 *
 * <p>Wraps a domain {@link ErrorCode} and exposes
 * application-specific error code and HTTP status.</p>
 */
@Getter
public class AppException extends RuntimeException {

    private final String responseMessage;
    private final int code;
    private final HttpStatus httpStatus;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.responseMessage = errorCode.getName();
        this.code = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }
}

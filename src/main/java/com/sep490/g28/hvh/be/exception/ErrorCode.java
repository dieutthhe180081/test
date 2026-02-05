package com.sep490.g28.hvh.be.exception;

import org.springframework.http.HttpStatus;

/**
 * Contract for application error codes.
 *
 * <p>Defines a numeric code, human-readable message,
 * and corresponding HTTP status.</p>
 */
public interface ErrorCode {
    int getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}

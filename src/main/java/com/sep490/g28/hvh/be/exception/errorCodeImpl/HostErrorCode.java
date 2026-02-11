package com.sep490.g28.hvh.be.exception.errorCodeImpl;

import com.sep490.g28.hvh.be.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Contain Error code related to Organization business rules
 * Code in format 5xxx
 */
@Getter
@AllArgsConstructor
public enum HostErrorCode implements ErrorCode {

    EMAIL_USED(5001, "Email đã được sử dụng bởi một host khác.", HttpStatus.BAD_REQUEST),

    ;
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

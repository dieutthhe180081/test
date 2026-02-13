package com.sep490.g28.hvh.be.exception.errorCodeImpl;

import com.sep490.g28.hvh.be.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Contain Error code related to Activity Domain/Activity Subdomain business rules
 * Code in format 6xxx
 */
@Getter
@AllArgsConstructor
public enum ActivityDomainErrorCode implements ErrorCode {

    DOMAIN_NOT_EXISTED(6001, "Lĩnh vực tình nguyện không tồn tại", HttpStatus.NOT_FOUND),
    SUBDOMAIN_NOT_EXISTED(6002, "Lĩnh vực tình nguyện con không tồn tại", HttpStatus.NOT_FOUND),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

package com.sep490.g28.hvh.be.exception.errorCodeImpl;

import com.sep490.g28.hvh.be.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Contain Error code related to Organization business rules
 * Code in format 4xxx
 */
@Getter
@AllArgsConstructor
public enum OrganizationErrorCode implements ErrorCode {

    REGISTRATION_NOT_EXISTED(4001, "Đơn đăng kí tổ chức không tồn tại.", HttpStatus.BAD_REQUEST),
    REGISTRATION_VERIFIED(4002, "Đơn đăng kí tổ chức đã được xác thực.", HttpStatus.BAD_REQUEST),
    EMAIL_USED(4003, "Email đã được sử dụng bởi một quản lý khác.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getName() {
        return this.name();
    }
}

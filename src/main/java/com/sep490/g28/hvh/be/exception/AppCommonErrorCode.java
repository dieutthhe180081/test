package com.sep490.g28.hvh.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppCommonErrorCode implements ErrorCode{
    //contain error from our be, like, cannot connect to other service, or something unexpect happen
    //start from 1000
    UNKNOWN_EXCEPTION(1000, "Lỗi chưa xác định. Hãy thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR ),
    UNAUTHENTICATED(1001, "Truy cập không xác thực. Access token không hợp lệ hoặc hết hạn.", HttpStatus.UNAUTHORIZED ),
    UNAUTHORIZED(1003, "Xin lỗi, bạn không có quyền truy cập.", HttpStatus.FORBIDDEN),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

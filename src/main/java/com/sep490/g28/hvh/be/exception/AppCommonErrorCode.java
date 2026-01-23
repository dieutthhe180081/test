package com.sep490.g28.hvh.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppCommonErrorCode implements ErrorCode{
    //contain error from our be, like, cannot connect to other service, or something unexpect happen
    //start from 1000
    UNKNOWN_EXCEPTION(1000, "Lỗi chưa xác định. Hãy thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1001, "Truy cập không xác thực. Access token không hợp lệ hoặc hết hạn.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "Xin lỗi, bạn không có quyền truy cập.", HttpStatus.FORBIDDEN),
    OTP_EXPIRED(1004, "OTP đã hết hạn.", HttpStatus.GONE),
    OTP_INVALID(1005, "OTP không đúng, hãy kiểm tra lại.", HttpStatus.UNAUTHORIZED),
    OTP_TOO_MANY_ATTEMPTS(1006, "Bạn đã nhập sai OTP quá nhiều lần. Hãy nhận lại OTP một lần nữa.", HttpStatus.TOO_MANY_REQUESTS),
    OTP_STILL_COOLDOWN(1007, "Bạn cần chờ ít nhất 60 giây từ lần nhận OTP trước để nhận lại OTP.", HttpStatus.TOO_MANY_REQUESTS),
    OTP_TOO_MANY_REQUESTS(1008, "Bạn đã gửi quá nhiểu yêu cầu nhận OTP. Hãy thử lại sau 7-10 phút nữa", HttpStatus.TOO_MANY_REQUESTS),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

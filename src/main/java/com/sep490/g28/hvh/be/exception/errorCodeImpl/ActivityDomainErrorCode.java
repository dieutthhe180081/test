package com.sep490.g28.hvh.be.exception.errorCodeImpl;

import com.sep490.g28.hvh.be.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ActivityDomainErrorCode implements ErrorCode {

    SESSION_TIME_OVERLAP(6001, "Thời gian kết thúc muộn nhất phải muộn hơn thời gian bắt đầu sớm nhất.", HttpStatus.BAD_REQUEST),
    SPECIAL_SESSION_EXCEEDS_DEFAULT(6002, "Thời gian tổ chức tối đa với lý do phải lớn hơn thời gian tổ chức tối đa mặc định.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

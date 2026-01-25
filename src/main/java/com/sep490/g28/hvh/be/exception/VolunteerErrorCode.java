package com.sep490.g28.hvh.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VolunteerErrorCode implements ErrorCode {

    CID_USED(3001, "Số căn cước công dân đã được sử dụng bởi một tình nguyện viên khác.", HttpStatus.BAD_REQUEST),
    EMAIL_USED(3001, "Email đã được sử dụng bởi một tình nguyện viên khác.", HttpStatus.BAD_REQUEST),
    PHONE_USED(3001, "Số điện thoại đã được sử dụng bởi một tình nguyện viên khác.", HttpStatus.BAD_REQUEST),
    NICKNAME_USED(3001, "Nickname đã được sử dụng bởi một tình nguyện viên khác.", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

package com.sep490.g28.hvh.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SupabaseErrorCode implements ErrorCode {

    //this will contain the exception mostly for developer to read
    VALIDATION_FAIL(9001, "Data gửi cho supabase sai, kiểm tra lại log và code", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(9002, "Service role để gửi request tới supabse sai, kiểm tra lại api secret key", HttpStatus.INTERNAL_SERVER_ERROR),
    RATE_LIMIT_EXCEEDED(9003, "Vuợt quá rate limit gửi request tới supabase", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(9004, "Supabase sập", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

package com.sep490.g28.hvh.be.exception;

import com.sep490.g28.hvh.be.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    ResponseEntity<ExceptionResponse<String>> appExceptionHandler(AppException e) {
        log.info("Exception is catch by appExceptionHandler, exception: {}", e.getMessage());
        var response = new ExceptionResponse<String>();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(response);
    }

    //todo thêm 1 caí resourceAccessException, SocketTimeoutException khi mình không thể gửi request cho bên thứ 3, third party unavailable

    //Note: This method should place in the final of this class to
    // use to log all the exception to console, incase something swallow the exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> catchAll(Exception ex) throws Exception {
        log.error("UNHANDLED EXCEPTION", ex);
        throw ex;
    }
}

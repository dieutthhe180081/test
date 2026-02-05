package com.sep490.g28.hvh.be.exception;

import com.sep490.g28.hvh.be.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for REST controllers.
 *
 * <p>Handles {@link AppException} explicitly and delegates
 * all other unhandled exceptions to the default mechanism
 * after logging.</p>
 */
@ControllerAdvice
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /**
     * Handles application-level exceptions.
     *
     * @param e application exception
     * @return standardized error response
     */
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

    /**
     * Catch-all handler for unexpected exceptions.
     *
     * <p>Logs the exception and rethrows it to avoid
     * silently swallowing errors.</p>
     */
    //Note: This method should place in the final of this class to
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> catchAll(Exception ex) throws Exception {
        log.error("UNHANDLED EXCEPTION", ex);
        throw ex;
    }
}

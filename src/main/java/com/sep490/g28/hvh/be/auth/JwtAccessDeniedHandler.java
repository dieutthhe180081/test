package com.sep490.g28.hvh.be.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.g28.hvh.be.dto.ExceptionResponse;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.AppCommonErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handles access denied (authorization) errors for JWT-protected resources.
 *
 * <p>This handler is triggered when an authenticated user attempts to access
 * a resource without sufficient permissions.</p>
 *
 * <p>It returns a JSON response with HTTP 403 (Forbidden) status
 * and a standardized error body.</p>
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        log.info("Unauthorized access");

        AppCommonErrorCode errorCode = AppCommonErrorCode.UNAUTHORIZED;
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //set header content type
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setCode(errorCode.getCode());
        exceptionResponse.setMessage(errorCode.name());
        exceptionResponse.setMoreInfo(Map.of("auth", errorCode.getMessage()));

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(exceptionResponse));
        response.getWriter().flush();
    }
}

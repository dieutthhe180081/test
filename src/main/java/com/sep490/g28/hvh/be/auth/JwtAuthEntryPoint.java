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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Handles authentication failures for JWT-based security.
 *
 * <p>This entry point is triggered when a request is made without valid
 * authentication credentials or when JWT validation fails.</p>
 *
 * <p>It returns a JSON response with HTTP 401 (Unauthorized) status
 * and a standardized error body.</p>
 */
@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.info("JWT Authentication Failed, go to JwtAutEntryPoint");

        String moreInfor = "Unauthenticated";
        if (authException.getCause() instanceof JwtException jwtEx) {
            moreInfor = jwtEx.getMessage(); // expired, invalid signature, etc.
        }

        AppCommonErrorCode errorCode = AppCommonErrorCode.UNAUTHENTICATED;
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //set header content type
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ExceptionResponse<String> apiResponse = new ExceptionResponse<>(errorCode.getCode(), errorCode.getMessage(), moreInfor);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
        response.getWriter().flush();

    }
}

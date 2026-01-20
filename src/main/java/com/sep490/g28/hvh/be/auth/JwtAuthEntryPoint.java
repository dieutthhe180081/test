package com.sep490.g28.hvh.be.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.g28.hvh.be.dto.ExceptionResponse;
import com.sep490.g28.hvh.be.exception.AppCommonErrorCode;
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

        String moreInfor = "Unauthorized";
        if (authException.getCause() instanceof JwtException jwtEx) {
            moreInfor = jwtEx.getMessage(); // expired, invalid signature, etc.
        }

        AppCommonErrorCode errorCode = AppCommonErrorCode.UNAUTHORIZED;
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //set header content type

        ExceptionResponse<String> apiResponse = new ExceptionResponse<>(errorCode.getCode(), errorCode.getMessage(), moreInfor);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
        response.getWriter().flush();

    }
}

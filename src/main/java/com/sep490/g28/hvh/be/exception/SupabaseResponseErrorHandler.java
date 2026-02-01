package com.sep490.g28.hvh.be.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep490.g28.hvh.be.dto.supabase.SupabaseErrorResponse;
import com.sep490.g28.hvh.be.mapper.SupabaseErrorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Custom {@link ResponseErrorHandler} for Supabase REST API calls.
 *
 * <p>Intercepts HTTP error responses, logs the raw response body,
 * maps Supabase-specific errors to {@link ErrorCode},
 * and throws {@link AppException}.</p>
 */
@Slf4j
public class SupabaseResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Checks whether the response indicates an error.
     *
     * @param response HTTP response
     * @return {@code true} if status is 4xx or 5xx
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    /**
     * Handles error responses returned by Supabase.
     *
     * @param url      request URI
     * @param method   HTTP method
     * @param response HTTP response containing error body
     */
    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.error("Supabase error raw body: {}", body);

        SupabaseErrorResponse errorResponse =
                objectMapper.readValue(body, SupabaseErrorResponse.class);
        throw new AppException(SupabaseErrorMapper.map(errorResponse));
    }
}

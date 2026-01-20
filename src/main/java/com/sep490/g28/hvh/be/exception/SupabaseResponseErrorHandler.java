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

@Slf4j
public class SupabaseResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.error("Supabase error raw body: {}", body);

        SupabaseErrorResponse errorResponse =
                objectMapper.readValue(body, SupabaseErrorResponse.class);
        throw new AppException(SupabaseErrorMapper.map(errorResponse));
    }
}

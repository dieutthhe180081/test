package com.sep490.g28.hvh.be.integration.authServer;

import com.sep490.g28.hvh.be.config.SupabaseConfig;
import com.sep490.g28.hvh.be.dto.supabase.CreateUserRequest;
import com.sep490.g28.hvh.be.dto.supabase.CreateUserResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@Service
public class SupabaseAuthService implements AuthService {
    private final RestTemplate restTemplate;
    private final SupabaseConfig supabaseConfig;

    public SupabaseAuthService(
            @Qualifier("supabaseRestTemplate") RestTemplate restTemplate,
            SupabaseConfig config
    ) {
        this.restTemplate = restTemplate;
        this.supabaseConfig = config;
    }

    @Override
    public UUID createUser(CreateUserRequest request) {
        String url = supabaseConfig.getUrl() + "/auth/v1/admin/users";

        HttpEntity<CreateUserRequest> httpEntity =
                new HttpEntity<>(request);

        ResponseEntity<CreateUserResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                CreateUserResponse.class
        );
        return Objects.requireNonNull(responseEntity.getBody()).id();
    }

}

package com.sep490.g28.hvh.be.integration.authServer;

import com.sep490.g28.hvh.be.config.SupabaseConfig;
import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.dto.supabase.CreateUserRequest;
import com.sep490.g28.hvh.be.dto.supabase.UserListResponse;
import com.sep490.g28.hvh.be.dto.supabase.UserResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
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
    public UUID createAccount(ERole role, String email, String password, String phone) {
        Map<String, Object> appMetadata = Map.of(
                "role", role.name(),
                "phone", phone
        );
        CreateUserRequest request = new CreateUserRequest(
                email,
                password,
                true,
                appMetadata
        );

        String url = supabaseConfig.getUrl() + "/auth/v1/admin/users";

        HttpEntity<CreateUserRequest> httpEntity =
                new HttpEntity<>(request);

        ResponseEntity<UserResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                UserResponse.class
        );
        return Objects.requireNonNull(responseEntity.getBody()).id();
    }

    @Override
    public boolean checkEmailExists(String email) {
        String url = supabaseConfig.getUrl()
                + "/auth/v1/admin/users?email=" + UriUtils.encode(email, StandardCharsets.UTF_8);

        ResponseEntity<UserListResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        UserListResponse.class
                );

        return response.getBody() != null
                && !response.getBody().users().isEmpty();
    }

}

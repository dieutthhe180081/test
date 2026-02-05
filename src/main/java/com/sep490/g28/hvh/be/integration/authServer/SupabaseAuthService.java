package com.sep490.g28.hvh.be.integration.authServer;

import com.sep490.g28.hvh.be.config.SupabaseProperties;
import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.dto.supabase.CreateUserRequest;
import com.sep490.g28.hvh.be.dto.supabase.UserListResponse;
import com.sep490.g28.hvh.be.dto.supabase.UserResponse;
import com.sep490.g28.hvh.be.exception.AppException;
import com.sep490.g28.hvh.be.exception.SupabaseException;
import com.sep490.g28.hvh.be.exception.errorCodeImpl.SupabaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Supabase-based implementation of {@link AuthService}.
 *
 * <p>Uses Supabase Admin REST API to manage user accounts.</p>
 *
 * <p>Notes:
 * <ul>
 *   <li>Requires Supabase service role key</li>
 *   <li>Performs server-to-server calls via {@link RestTemplate}</li>
 *   <li>Stores role and phone number in {@code app_metadata}</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
public class SupabaseAuthService implements AuthService {
    private final RestTemplate restTemplate;
    private final SupabaseProperties supabaseProperties;

    public SupabaseAuthService(
            @Qualifier("supabaseRestTemplate") RestTemplate restTemplate,
            SupabaseProperties config
    ) {
        this.restTemplate = restTemplate;
        this.supabaseProperties = config;
    }

    /**
     * Creates a Supabase user using Admin API.
     *
     * <p>The user is created as email-verified by default
     * and includes custom {@code app_metadata}.</p>
     *
     * @throws AppException if Supabase returns an error
     */
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

        String url = supabaseProperties.getUrl() + "/auth/v1/admin/users";

        HttpEntity<CreateUserRequest> httpEntity =
                new HttpEntity<>(request);

        try {
            ResponseEntity<UserResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    UserResponse.class
            );
            UUID id = Objects.requireNonNull(responseEntity.getBody()).id();
            log.info("Create user id={}", id);
            return id;
        } catch (SupabaseException e){
            throw new AppException(SupabaseErrorCode.AUTH_CREATE_ACCOUNT_FAIL);
        }

    }

    /**
     * Checks email existence via Supabase Admin API.
     *
     * @param email email to check
     * @return {@code true} if at least one user is found
     */
    @Override
    public boolean checkEmailExists(String email) {
        String url = supabaseProperties.getUrl()
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

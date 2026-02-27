package com.sep490.g28.hvh.be.integration.authServer.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String phone,

        Map<String, Object> user_metadata,   // public metadata
        Map<String, Object> app_metadata,    // admin metadata (role, phone custom...)

        OffsetDateTime banned_until,

        OffsetDateTime created_at,
        OffsetDateTime last_sign_in_at

) {}


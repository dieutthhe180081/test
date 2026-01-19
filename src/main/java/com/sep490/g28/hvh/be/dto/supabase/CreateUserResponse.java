package com.sep490.g28.hvh.be.dto.supabase;

import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String email
) {}


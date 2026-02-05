package com.sep490.g28.hvh.be.dto.supabase;

import java.util.Map;

public record CreateUserRequest (
        String email,
        String password,
        Boolean email_confirm,
        Map<String, Object> app_metadata
) {}

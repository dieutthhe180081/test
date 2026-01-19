package com.sep490.g28.hvh.be.dto.supabase;

public record CreateUserRequest (
        String email,
        String password,
        String phone
) {}

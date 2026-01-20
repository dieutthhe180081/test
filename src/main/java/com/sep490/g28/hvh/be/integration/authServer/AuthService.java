package com.sep490.g28.hvh.be.integration.authServer;

import com.sep490.g28.hvh.be.dto.supabase.CreateUserRequest;

import java.util.UUID;

public interface AuthService {
    UUID createUser(CreateUserRequest request);
}

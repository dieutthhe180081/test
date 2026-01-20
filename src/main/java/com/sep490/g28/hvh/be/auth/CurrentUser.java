package com.sep490.g28.hvh.be.auth;

import com.sep490.g28.hvh.be.constant.ERole;

import java.util.UUID;

public record CurrentUser(
        UUID id,
        String email,
        ERole roleName
) {
}

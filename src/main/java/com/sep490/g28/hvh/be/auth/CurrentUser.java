package com.sep490.g28.hvh.be.auth;

import com.sep490.g28.hvh.be.constant.ERole;

import java.util.UUID;

/**
 * Represents the currently authenticated user.
 *
 * <p>Typically used in authentication/authorization context
 * to pass user information across layers (Security, Service, Controller).</p>
 *
 * @param id       Unique identifier of the user (UUID).
 * @param email    User's email, used for authentication and identification.
 * @param roleName User's role in the system.
 */
public record CurrentUser(
        UUID id,
        String email,
        ERole roleName
) {
}

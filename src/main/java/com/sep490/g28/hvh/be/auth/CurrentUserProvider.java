package com.sep490.g28.hvh.be.auth;

import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.entity.User;
import com.sep490.g28.hvh.be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

/**
 * Extract JWT claim to put it in CurrentUser, representing the user who making the current request
 */
@Component
@RequestScope
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;
    private CurrentUser currentUser;

    private CurrentUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CurrentUser) auth.getDetails();
    }

    public UUID getId() {
        return get().id();
    }

    public String getEmail() {
        return get().email();
    }

    public ERole getRoleName() {
        return get().roleName();
    }
}

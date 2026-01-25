package com.sep490.g28.hvh.be.integration.authServer;

import com.sep490.g28.hvh.be.constant.ERole;

import java.util.UUID;

public interface AuthService {
    UUID createAccount(ERole role, String email, String password, String phone);
    boolean checkEmailExists(String email);
}

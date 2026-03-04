package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.dto.auth.request.ForgotPasswordRequest;

public interface AuthService {
    void forgotPassword(ForgotPasswordRequest request);
}

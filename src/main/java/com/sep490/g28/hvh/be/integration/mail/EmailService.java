package com.sep490.g28.hvh.be.integration.mail;

public interface EmailService {
    void sendApproveRegisterVolAccountEmail(String userEmail);

    void sendVerifyRegisterOtp(String email, String otp);

    void sendVerifyForgotPasswordOtp(String email, String otp);
}

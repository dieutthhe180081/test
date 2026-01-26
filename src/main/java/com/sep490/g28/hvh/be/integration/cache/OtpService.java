package com.sep490.g28.hvh.be.integration.cache;

public interface OtpService {
    String getVerifyRegisterOtp(String email);
    boolean verifyVerifyRegisterOtp(String email, String inputOtp);

    String getVerifyForgotPasswordOtp(String email);
    boolean verifyVerifyForgotPasswordOtp(String email, String inputOtp);
}

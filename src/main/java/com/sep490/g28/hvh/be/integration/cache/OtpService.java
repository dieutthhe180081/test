package com.sep490.g28.hvh.be.integration.cache;

public interface OtpService {
    String getVerifyEmailOtp(String email);
    boolean verifyVerifyEmailOtp(String email, String inputOtp);

}

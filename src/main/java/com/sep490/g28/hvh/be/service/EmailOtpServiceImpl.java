package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.integration.cache.OtpService;
import com.sep490.g28.hvh.be.integration.mail.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailOtpServiceImpl implements EmailOtpService {

    OtpService otpService;
    EmailService emailService;

    @Override
    public void sendVerifyRegisterOtp(String email){
        String otp = otpService.getVerifyRegisterOtp(email);
        emailService.sendVerifyRegisterOtp(email, otp);
    }

//    @Override
//    public void sendVerifyForgotPasswordOtp(String email) {
//        String otp = otpService.getVerifyForgotPasswordOtp(email);
//        emailService.sendVerifyForgotPasswordOtp(email, otp);
//    }
}
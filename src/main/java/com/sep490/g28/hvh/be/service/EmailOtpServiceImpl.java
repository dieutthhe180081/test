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
    public void sendVerifyVolAccountRegistrationOtp(String email){
        String otp = otpService.getVolAccountRegistrationOtp(email);
        emailService.sendVolAccountRegistrationOtp(email, otp);
    }

    @Override
    public void sendVerifyOrganizationRegistrationOtp(String email){
        String otp = otpService.getOrgRegistrationOtp(email);
        emailService.sendOrgRegistrationOtp(email, otp);
    }


//    @Override
//    public void sendVerifyForgotPasswordOtp(String email) {
//        String otp = otpService.getVerifyForgotPasswordOtp(email);
//        emailService.sendVerifyForgotPasswordOtp(email, otp);
//    }
}
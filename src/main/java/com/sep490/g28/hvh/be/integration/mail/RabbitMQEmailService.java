package com.sep490.g28.hvh.be.integration.mail;

import com.sep490.g28.hvh.be.integration.messagequeue.MailPublisher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/**
 * EmailService implementation using RabbitMQ.
 * <p>
 * Emails are published to a queue for async processing.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Prepare email subject and body</li>
 *   <li>Publish email jobs to message queue</li>
 * </ul>
 *
 * <p>Does NOT:</p>
 * <ul>
 *   <li>Send emails directly</li>
 *   <li>Handle SMTP failures</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RabbitMQEmailService implements EmailService {

    MailPublisher mailPublisher;

    @Override
    public void sendApproveRegisterVolAccountEmail(String userEmail, String password) {
        String subject = "HVH - Chào mừng bạn";
        String body = String.format("""
                Chúc mừng bạn đã đăng kí tài khoản thành tình nguyện viên thành công trên app Hà Nội Volunteer Hub!
                Hãy sử dụng email này cùng với mật khẩu dưới đây để đăng nhập vào hệ thống.
                Mật khẩu mặc định: %s
                """, password);
        mailPublisher.enqueue(userEmail, subject, body);
    }

    @Override
    public void sendVolAccountRegistrationOtp(String email, String otp) {
        String subject = "HVH - Xác nhận email";
        String body = String.format("""
                Chào bạn, chúng tôi gửi mail này nhằm xác nhận rằng bạn đang sử dụng email này để đăng kí tài khoản tình nguyện viên qua app Hà Nội Volunteer Hub.
                Hãy sử dụng mã OTP dưới đây dể xác nhận.
                Mã OTP: %s
                """, otp);
        mailPublisher.enqueue(email, subject, body);
    }

    @Override
    public void sendOrgRegistrationOtp(String email, String otp) {
        String subject = "HVH - Xác nhận email dăng kí tổ chức";
        String body = String.format("""
                Chào bạn, chúng tôi gửi mail này nhằm xác nhận rằng bạn đang sử dụng email này để đăng kí một tổ chức tình nguyện trên hệ thống Hà Nội Volunteer Hub.
                Hãy sử dụng mã OTP dưới đây dể xác nhận.
                Mã OTP: %s
                """, otp);
        mailPublisher.enqueue(email, subject, body);
    }

    @Override
    public void sendVerifyForgotPasswordOtp(String email, String otp) {
        String subject = "HVH - Xác nhận yêu cầu khôi phục mật khẩu";
        String body = String.format("""
                Chào bạn, chúng tôi gửi mail này nhằm xác nhận rằng bạn đang sử dụng email này để khôi phục mật khẩu đăng nhập Hà Nội Volunteer Hub.
                Hãy sử dụng mã OTP dưới đây dể xác nhận.
                Mã OTP: %s
                """, otp);
        mailPublisher.enqueue(email, subject, body);
    }
}
package com.sep490.g28.hvh.be.integration.mail;

/**
 * Email service abstraction.
 * <p>
 * Defines business-level email use cases.
 * This layer must NOT expose transport details (SMTP, RabbitMQ, etc).
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Define WHAT email is sent</li>
 *   <li>Hide HOW email is delivered</li>
 * </ul>
 */
public interface EmailService {

    /**
     * Send approval email after volunteer account registration is accepted.
     *
     * @param userEmail recipient email
     */
    void sendApproveRegisterVolAccountEmail(String userEmail);

    /**
     * Send OTP email for volunteer account registration verification.
     *
     * @param email recipient email
     * @param otp   verification OTP
     */
    void sendVolAccountRegistrationOtp(String email, String otp);

    /**
     * Send OTP email for organization registration verification.
     *
     * @param email recipient email
     * @param otp   verification OTP
     */
    void sendOrgRegistrationOtp(String email, String otp);

    /**
     * Send OTP email for forgot-password flow.
     *
     * @param email recipient email
     * @param otp   verification OTP
     */
    void sendVerifyForgotPasswordOtp(String email, String otp);
}

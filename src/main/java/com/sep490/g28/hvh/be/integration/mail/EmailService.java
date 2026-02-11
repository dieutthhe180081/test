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
     * @param password default password of the account
     */
    void sendApproveRegisterVolAccountEmail(String userEmail, String password);

    /**
     * Send reject email after volunteer account registration is rejected.
     *
     * @param userEmail recipient email
     * @param rejectionReason rejection reason
     */
    void sendRejectRegisterVolAccountEmail(String userEmail, String rejectionReason);

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

    /**
     * Send approval email after organization registration is accepted.
     *
     * @param orgName organization name
     * @param userEmail recipient email
     * @param password default password of the account
     */
    void sendApproveRegisterOrganizationEmail(String orgName, String userEmail, String password);

    /**
     * Send reject email after organization registration is rejected.
     *
     * @param userEmail recipient email
     * @param rejectionReason rejection reason
     */
    void sendRejectRegisterOrganizationEmail(String userEmail, String rejectionReason);

    /**
     * Send approval email after organization registration is accepted.
     *
     * @param orgName organization name
     * @param hostEmail recipient email
     * @param password default password of the account
     */
    void sendCreateHostAccountEmail(String orgName, String hostEmail, String password);
}

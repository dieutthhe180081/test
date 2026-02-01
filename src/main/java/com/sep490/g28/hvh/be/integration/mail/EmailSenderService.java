package com.sep490.g28.hvh.be.integration.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Low-level email sender using SMTP.
 * <p>
 * This class is a technical utility, not a business service.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Build MIME messages</li>
 *   <li>Send email via {@link JavaMailSender}</li>
 * </ul>
 *
 * <p>Note:</p>
 * <ul>
 *   <li>Does NOT contain business logic</li>
 *   <li>Throws {@link MessagingException} on SMTP failures</li>
 * </ul>
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailSenderService {
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    @NonFinal
    String fromEmail;

    public void sendEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
    }

}

package com.sep490.g28.hvh.be.integration.mail;

import com.sep490.g28.hvh.be.integration.messagequeue.MailPublisher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RabbitMQMailService implements MailService {

    MailPublisher mailPublisher;

    @Override
    public void sendApproveRegisterVolAccountEmail(String userEmail) {
        String subject = "HVH - Chào mừng bạn";
        String body = "test gửi mail ";
        mailPublisher.enqueue(userEmail, subject, body);
    }
}
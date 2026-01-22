package com.sep490.g28.hvh.be.integration.messagequeue;

import com.sep490.g28.hvh.be.config.RabbitMqMailProperties;
import com.sep490.g28.hvh.be.dto.rabbitmq.MailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailPublisher {

    private final RabbitMqMailProperties properties;
    private final RabbitTemplate rabbitTemplate;

    public void enqueue(String to, String subject, String body) {
        MailMessage msg = new MailMessage(to, subject, body);

        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.queue().send(),
                msg
        );
    }
}
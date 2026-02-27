package com.sep490.g28.hvh.be.integration.email;

import com.sep490.g28.hvh.be.config.RabbitMqEmailProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ publisher for email messages.
 * <p>
 * Publishes email jobs to message queue for asynchronous processing.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Create {@link MailMessage}</li>
 *   <li>Publish to configured exchange and routing key</li>
 * </ul>
 *
 * <p>Does NOT:</p>
 * <ul>
 *   <li>Send email directly</li>
 *   <li>Handle retry or failure logic</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class MailPublisher {

    private final RabbitMqEmailProperties properties;
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
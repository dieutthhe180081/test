package com.sep490.g28.hvh.be.integration.messagequeue;

import com.sep490.g28.hvh.be.config.RabbitMqEmailProperties;
import com.sep490.g28.hvh.be.dto.rabbitmq.EmailMessage;
import com.sep490.g28.hvh.be.integration.mail.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * RabbitMQ consumer for email messages.
 * <p>
 * Listens to email queues and delegates actual sending to {@link EmailSenderService}.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Consume email messages from RabbitMQ</li>
 *   <li>Trigger email sending</li>
 *   <li>Handle failures and route messages to DLQ</li>
 * </ul>
 *
 * <p>Error handling:</p>
 * <ul>
 *   <li>On send failure, throws {@link AmqpRejectAndDontRequeueException}</li>
 *   <li>Message will NOT be re-queued</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailConsumer {

    private final EmailSenderService emailSenderService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqEmailProperties properties;

    /**
     * Consume email message from main send queue.
     *
     * @param msg email payload
     */
    @RabbitListener(queues = "${rabbitmq.mail.queue.send}")
    public void consume(
            EmailMessage msg
    ) {
        try {
            emailSenderService.sendEmail(
                    msg.getTo(),
                    msg.getSubject(),
                    msg.getBody()
            );
        } catch (Exception e) {
            log.error("Send message failed: {}", msg);
            throw new AmqpRejectAndDontRequeueException("MAIL_SEND_FAILED");
        }
    }

    /**
     * Consume message from Dead Letter Queue (DLQ).
     * <p>
     * Triggered after retry limit is exceeded.
     * </p>
     *
     * @param message raw AMQP message (headers, metadata)
     * @param msg     deserialized email payload
     */
    @RabbitListener(queues = "${rabbitmq.mail.queue.dlq}")
    public void consumeDlq(Message message, EmailMessage msg) {
        MessageProperties props = message.getMessageProperties();

        List<Map<String, Object>> deaths =
                (List<Map<String, Object>>) props.getHeaders().get("x-death");

        long retryCount = deaths == null ? 0 :
                deaths.stream()
                        .mapToLong(d -> (Long) d.get("count"))
                        .sum();

        log.error(
                "MAIL FAILED after {} retries, reason={}",
                retryCount,
                props.getHeaders().get("x-first-death-reason")
        );
        log.error("DLQ MESSAGE: {}", msg);
    }
}



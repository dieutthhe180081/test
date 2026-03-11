package com.sep490.g28.hvh.be.integration.email;

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
public class EmailConsumer {

    private final EmailSenderService emailSenderService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqEmailProperties properties;

    /**
     * Consume email message from main send queue.
     *
     * @param msg email payload
     */
    @RabbitListener(queues = "${rabbitmq.email.queue.send}")
    public void consume(
            Message message,
            EmailMessage msg
    ) {
        int retryCount = getRetryCountForSendQueue(message);

        if (retryCount >= properties.retry().maxAttempts() - 1) {
            // exceed max attempts -> send to dlq
            rabbitTemplate.send(
                    properties.exchange(),
                    properties.routing().dlq(),
                    message
            );
            return;
        }

        try {
            //retry send email
            emailSenderService.sendEmail(
                    msg.getTo(),
                    msg.getSubject(),
                    msg.getBody()
            );
        } catch (Exception e) {
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
    @RabbitListener(queues = "${rabbitmq.email.queue.dlq}")
    public void consumeDlq(Message message, EmailMessage msg) {
        MessageProperties props = message.getMessageProperties();

        int retryCount = getRetryCountForSendQueue(message);

        log.error(
                "MAIL FAILED after {} retries, reason={}",
                retryCount,
                props.getHeaders().get("x-first-death-reason")
        );
        log.error("DLQ MESSAGE: {}", msg);
    }

    /**
     * Extract the times the message is retried in queue send
     *
     * @param message raw AMQP message (headers, metadata)
     * @return int as number of retry times
     */
    private int getRetryCountForSendQueue(Message message) {

        List<Map<String, Object>> deaths =
                (List<Map<String, Object>>) message
                        .getMessageProperties()
                        .getHeaders()
                        .get("x-death");

        if (deaths == null) return 0;

        log.info(deaths.toString());

        return deaths.stream()
                .filter(d -> properties.queue().send().equals(d.get("queue")))
                .mapToInt(d -> ((Long) d.get("count")).intValue())
                .sum();
    }
}



package com.sep490.g28.hvh.be.integration.messagequeue;

import com.sep490.g28.hvh.be.config.RabbitMqMailProperties;
import com.sep490.g28.hvh.be.dto.rabbitmq.MailMessage;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class MailConsumer {

    private final EmailSenderService emailSenderService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqMailProperties properties;

    @RabbitListener(queues = "${rabbitmq.mail.queue.send}")
    public void consume(
            MailMessage msg
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

    @RabbitListener(queues = "${rabbitmq.mail.queue.dlq}")
    public void consumeDlq(Message message, MailMessage msg) {
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
        // TODO: save DB / alert / manual requeue/ log or do st else
    }
}



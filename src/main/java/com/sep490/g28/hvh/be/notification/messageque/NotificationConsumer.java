package com.sep490.g28.hvh.be.notification.messageque;

import com.sep490.g28.hvh.be.notification.config.RabbitMqNotificationProperties;
import com.sep490.g28.hvh.be.notification.dto.TopicSubscriptionMessage;
import com.sep490.g28.hvh.be.notification.entity.Notification;
import com.sep490.g28.hvh.be.notification.exception.NonRetryableFcmException;
import com.sep490.g28.hvh.be.notification.sender.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final PushNotificationSender pushNotificationSender;
    private final RabbitMqNotificationProperties properties;
    private final RabbitTemplate rabbitTemplate;


    /**
     * Extract the times the message is retried in queue
     *
     * @param message raw AMQP message (headers, metadata)
     * @param queueName the name of the queue
     * @return int as number of retry times
     */
    private int getRetryCountForQueue(Message message, String queueName) {

        List<Map<String, Object>> deaths =
                (List<Map<String, Object>>) message
                        .getMessageProperties()
                        .getHeaders()
                        .get("x-death");

        if (deaths == null) return 0;

        log.info(deaths.toString());

        return deaths.stream()
                .filter(d -> queueName.equals(d.get("queue")))
                .mapToInt(d -> ((Long) d.get("count")).intValue())
                .sum();
    }

// =========================================================
    @RabbitListener(queues = "${rabbitmq.notification.queue.send-user}")
    public void consumeSendUser(
            Message message,
            Notification notification) {

        int retryCount = getRetryCountForQueue(message, properties.queue().sendUser());
        if (retryCount >= properties.retry().maxAttempts() - 1) {
            // exceed max attempts -> send to dlq
            rabbitTemplate.send(
                    properties.exchange(),
                    properties.routing().dlqUser(),
                    message
            );
            return;
        }

        try {
            //retry send notification
            pushNotificationSender.sendMulticast(notification);
        } catch (NonRetryableFcmException e) {

            // send to dlq immediately
            rabbitTemplate.send(
                    properties.exchange(),
                    properties.routing().dlqUser(),
                    message
            );

        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("RETRY");
        }
    }

    @RabbitListener(queues = "${rabbitmq.notification.queue.dlq-user}")
    public void consumeDlqUser(Message message, Notification notification) {
        MessageProperties props = message.getMessageProperties();

        int retryCount = getRetryCountForQueue(message, properties.queue().dlqUser());

        log.error(
                "NOTIFICATION SENT FAILED after {} retries, reason={}",
                retryCount,
                props.getHeaders().get("x-first-death-reason")
        );
        log.error("DLQ MESSAGE: notification={}", notification);
    }

    @RabbitListener(queues = "${rabbitmq.notification.queue.send-topic}")
    public void consumeSendTopic(
            Message message,
            Notification notification) {

        int retryCount = getRetryCountForQueue(message, properties.queue().sendTopic());

        try {
            //retry send notification
            pushNotificationSender.sendMulticast(notification);
        } catch (NonRetryableFcmException e) {
            // send to dlq immediately
            rabbitTemplate.send(
                    properties.exchange(),
                    properties.routing().dlqTopic(),
                    message
            );

        } catch (Exception e) {
            if (retryCount >= properties.retry().maxAttempts() - 1) {
                // exceed max attempts -> send to dlq
                rabbitTemplate.send(
                        properties.exchange(),
                        properties.routing().dlqTopic(),
                        message
                );
            }else {
                throw new AmqpRejectAndDontRequeueException("RETRY");
            }
        }
    }

    @RabbitListener(queues = "${rabbitmq.notification.queue.dlq-topic}")
    public void consumeDlqTopic(Message message, Notification notification) {
        MessageProperties props = message.getMessageProperties();

        int retryCount = getRetryCountForQueue(message, properties.queue().dlqUser());

        log.error(
                "NOTIFICATION SENT TO TOPIC FAILED after {} retries, reason={}",
                retryCount,
                props.getHeaders().get("x-first-death-reason")
        );
        log.error("DLQ MESSAGE: notification={}", notification);
    }


    //    =====================================================
    @RabbitListener(queues = "${rabbitmq.notification.queue.subscribe}")
    public void consumeSubscribe(
            Message message,
            TopicSubscriptionMessage msg) {

        int retryCount =
                getRetryCountForQueue(message, properties.queue().subscribe());

        try {
            pushNotificationSender.subscribeToTopics(msg.getToken(), msg.getTopics());
        } catch (NonRetryableFcmException e) {
            // send to dlq immediately
            rabbitTemplate.send(
                    properties.exchange(),
                    properties.routing().subscribeDlq(),
                    message
            );

        } catch (Exception e) {
            if (retryCount >= properties.retry().maxAttempts() - 1) {
                // exceed max attempts -> send to dlq
                rabbitTemplate.send(
                        properties.exchange(),
                        properties.routing().subscribeDlq(),
                        message
                );
            } else {
                throw new AmqpRejectAndDontRequeueException("RETRY");
            }
        }
    }

    @RabbitListener(queues = "${rabbitmq.notification.queue.subscribe-dlq}")
    public void consumeDlqSubscribe(Message message, TopicSubscriptionMessage msg) {
        MessageProperties props = message.getMessageProperties();

        int retryCount = getRetryCountForQueue(message, properties.queue().subscribeDlq());

        log.error(
                "SUBSCRIBE TO TOPIC FAIL after {} retries, reason={}",
                retryCount,
                props.getHeaders().get("x-first-death-reason")
        );
        log.error("DLQ MESSAGE: topicSubscriptionMessage={}", msg);
    }


    //    =====================================================
    @RabbitListener(queues = "${rabbitmq.notification.queue.unsubscribe}")
    public void consumeUnsubscribe(
            Message message,
            TopicSubscriptionMessage msg) {

        int retryCount =
                getRetryCountForQueue(message, properties.queue().unsubscribe());

        try {
            pushNotificationSender.unsubscribeFromTopics(msg.getToken(), msg.getTopics());
        } catch (NonRetryableFcmException e) {
            // send to dlq immediately
            rabbitTemplate.send(
                    properties.exchange(),
                    properties.routing().unsubscribeDlq(),
                    message
            );

        } catch (Exception e) {
            if (retryCount >= properties.retry().maxAttempts() - 1) {
                // exceed max attempts -> send to dlq
                rabbitTemplate.send(
                        properties.exchange(),
                        properties.routing().unsubscribeDlq(),
                        message
                );
            } else {
                throw new AmqpRejectAndDontRequeueException("RETRY");
            }
        }
    }

    @RabbitListener(queues = "${rabbitmq.notification.queue.unsubscribe-dlq}")
    public void consumeDlqUnsubscribe(Message message, TopicSubscriptionMessage msg) {
        MessageProperties props = message.getMessageProperties();

        int retryCount = getRetryCountForQueue(message, properties.queue().unsubscribeDlq());

        log.error(
                "UNSUBSCRIBE TO TOPIC FAIL after {} retries, reason={}",
                retryCount,
                props.getHeaders().get("x-first-death-reason")
        );
        log.error("DLQ MESSAGE: topicSubscriptionMessage={}", msg);
    }
}
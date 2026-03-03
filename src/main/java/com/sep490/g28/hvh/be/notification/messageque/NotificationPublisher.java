package com.sep490.g28.hvh.be.notification.messageque;

import com.sep490.g28.hvh.be.notification.config.RabbitMqNotificationProperties;
import com.sep490.g28.hvh.be.notification.dto.TopicSubscriptionMessage;
import com.sep490.g28.hvh.be.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqNotificationProperties properties;

    public void enqueueNotification(Notification notification) {
        if (notification.getUser() != null) {
            rabbitTemplate.convertAndSend(
                    properties.exchange(),
                    properties.routing().sendUser(),
                    notification
            );
        } else if (notification.getTopic() != null) {
            rabbitTemplate.convertAndSend(
                    properties.exchange(),
                    properties.routing().sendTopic(),
                    notification
            );
        } else throw new IllegalArgumentException("Missing target in notification, check your code");
    }


    public void enqueueSubscribeToTopics(String token, Collection<String> topics) {

        TopicSubscriptionMessage payload =
                new TopicSubscriptionMessage(token, topics);

        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routing().subscribe(),
                payload
        );
    }

        public void enqueueUnsubscribeFromTopics(String token, Collection<String> topics) {

        TopicSubscriptionMessage payload =
                new TopicSubscriptionMessage(token, topics);

        rabbitTemplate.convertAndSend(
                properties.exchange(),
                properties.routing().unsubscribe(),
                payload
        );
    }

}

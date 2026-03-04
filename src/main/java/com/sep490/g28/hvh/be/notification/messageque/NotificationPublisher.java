package com.sep490.g28.hvh.be.notification.messageque;

import com.sep490.g28.hvh.be.notification.config.RabbitMqNotificationProperties;
import com.sep490.g28.hvh.be.notification.dto.SendNotificationMessage;
import com.sep490.g28.hvh.be.notification.dto.TopicSubscriptionMessage;
import com.sep490.g28.hvh.be.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqNotificationProperties properties;

    //todo vieets doc su dung
    public void enqueueNotification(Notification notification, UUID targetUserId) {
        SendNotificationMessage payload = SendNotificationMessage.builder()
                .notificationId(notification.getId())
                .userId(targetUserId)
                .topic(notification.getTopic())
                .title(notification.getTitle())
                .body(notification.getBody())
                .data(notification.getData())
                .build();

        if (notification.getTopic() != null && !notification.getTopic().isBlank()) {
            //put message to queue send user
            rabbitTemplate.convertAndSend(
                    properties.exchange(),
                    properties.routing().sendTopic(),
                    payload
            );
        } else if (targetUserId != null) {
            //put message to queue send topic
            rabbitTemplate.convertAndSend(
                    properties.exchange(),
                    properties.routing().sendUser(),
                    payload
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

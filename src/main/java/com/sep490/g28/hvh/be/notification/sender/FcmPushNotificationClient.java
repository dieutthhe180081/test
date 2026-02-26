package com.sep490.g28.hvh.be.notification.sender;

import com.google.firebase.messaging.*;
import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FcmPushNotificationClient implements PushNotificationClient {

    // FCM limit 500 token / request
    private static final int BATCH_SIZE = 500;
    private static final String GLOBAL_TOPIC = "global";

    @Async("pushExecutor")
    @Override
    public void sendToToken(String token, NotificationPayload payload) {
        Message msg = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(payload.getTitle())
                                .setBody(payload.getBody())
                                .build()
                )
//                .putData("ACTION", "test")
//                .putAllData(payload.getData())
                .putAllData(
                        payload.getData() == null ? Map.of() : payload.getData()
                )
                .build();
        try {
            String msgId = FirebaseMessaging.getInstance().send(msg);
            log.info("Send message to token={} id={}", token, msgId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM send failed", e);
        }
    }

    @Override
    public void sendMulticast(List<String> tokens, NotificationPayload payload) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        for (int i = 0; i < tokens.size(); i += BATCH_SIZE) {
            List<String> batch =
                    tokens.subList(i, Math.min(i + BATCH_SIZE, tokens.size()));

            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(batch)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(payload.getTitle())
                                    .setBody(payload.getBody())
                                    .build()
                    )
                    .putAllData(
                            payload.getData() == null ? Map.of() : payload.getData()
                    )
                    .build();

            try {
                BatchResponse response =
                        FirebaseMessaging.getInstance().sendEachForMulticast(message);

                log.info(
                        "Push sent: success={}, failure={}",
                        response.getSuccessCount(),
                        response.getFailureCount()
                );

            } catch (FirebaseMessagingException e) {
                log.error("FCM send failed", e);
            }
        }
    }

    @Override
    public void subscribeToTopic(String token, String topic) {
        try {
        FirebaseMessaging.getInstance()
                .subscribeToTopic(List.of(token), topic);
            log.info("Subscribe to topic={} to token={}", topic, token);
        } catch (FirebaseMessagingException e) {
            log.error("FCM subscribe failed", e);
        }
    }

    @Override
    public void unsubscribeFromTopic(String token, String topic) {
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(List.of(token), topic);
            log.info("Unsubscribe to topic={} to token={}", topic, token);
        } catch (FirebaseMessagingException e) {
            log.error("FCM subscribe failed", e);
        }
    }

    @Override
    public void sendToTopic(String topic, NotificationPayload payload) {
        Message msg = Message.builder()
                .setTopic(topic)
                .setNotification(
                        Notification.builder()
                                .setTitle(payload.getTitle())
                                .setBody(payload.getBody())
                                .build()
                )
                .putAllData(
                        payload.getData() == null ? Map.of() : payload.getData()
                )
                .build();
        try {
            String msgId = FirebaseMessaging.getInstance().send(msg);
            log.info("Send message to topic={} id={}", topic, msgId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM send failed", e);
        }
    }


}

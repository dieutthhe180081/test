package com.sep490.g28.hvh.be.notification.sender;

import com.google.firebase.messaging.*;
import com.sep490.g28.hvh.be.notification.dto.PushNotificationPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class FcmPushNotificationSender implements PushNotificationSender {

    // FCM giới hạn 500 token / request
    private static final int BATCH_SIZE = 500;

//    @Async("pushExecutor")
//    @Override
//    public void sendToTokens(List<String> tokens, PushPayload payload) {
//
//        if (tokens == null || tokens.isEmpty()) {
//            return;
//        }
//
//        for (int i = 0; i < tokens.size(); i += BATCH_SIZE) {
//            List<String> batch =
//                    tokens.subList(i, Math.min(i + BATCH_SIZE, tokens.size()));
//
//            MulticastMessage message = MulticastMessage.builder()
//                    .addAllTokens(batch)
//                    .setNotification(
//                            Notification.builder()
//                                    .setTitle(payload.getTitle())
//                                    .setBody(payload.getBody())
//                                    .build()
//                    )
//                    .putAllData(
//                            payload.getData() == null ? Map.of() : payload.getData()
//                    )
//                    .build();
//
//            try {
//                BatchResponse response =
//                        FirebaseMessaging.getInstance().sendMulticast(message);
//
//                log.info(
//                        "Push sent: success={}, failure={}",
//                        response.getSuccessCount(),
//                        response.getFailureCount()
//                );
//
//            } catch (FirebaseMessagingException e) {
//                log.error("FCM send failed", e);
//            }
//        }
//    }
//
//    @Async("pushExecutor")
//    @Override
//    public void broadcast(PushPayload payload) {
//        // Cách 1: topic-based broadcast
//        Message message = Message.builder()
//                .setTopic("global")
//                .setNotification(
//                        Notification.builder()
//                                .setTitle(payload.getTitle())
//                                .setBody(payload.getBody())
//                                .build()
//                )
//                .putAllData(
//                        payload.getData() == null ? Map.of() : payload.getData()
//                )
//                .build();
//
//        try {
//            FirebaseMessaging.getInstance().send(message);
//        } catch (FirebaseMessagingException e) {
//            log.error("FCM broadcast failed", e);
//        }
//    }
//
//    @Override
//    public void sendToTopic(String topic, PushPayload payload) {
//        Message msg = Message.builder()
//                .setTopic(topic)
//                .putData("body", "some data")
//                .build();
//
//        try {
//            String msgId = FirebaseMessaging.getInstance().send(msg);
//            log.info("Send message to topic id={}", msgId);
//        } catch (FirebaseMessagingException e) {
//            log.error("FCM send failed", e);
//        }
//
//    }

    @Override
    public void sendToToken(String token, PushNotificationPayload payload) {
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
}

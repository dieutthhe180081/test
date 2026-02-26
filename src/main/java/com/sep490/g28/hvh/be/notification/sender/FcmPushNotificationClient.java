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

    private void handleFirebaseMessagingException(FirebaseMessagingException e, String token) {
        MessagingErrorCode code = e.getMessagingErrorCode();

            switch (code) {

                case UNREGISTERED:
                    //token not exist or died
                    // todo token chết → xóa DB
                    //tim xem co khong da
                    if (token != null && !token.isEmpty()) {
//                    userFcmTokenRepository.deleteByToken(token);
                    log.warn("FCM token unregistered, deleted: {}", token);

                    }
                    break;

                case INVALID_ARGUMENT:
                    log.error("Invalid FCM payload/token", e);
                    break;

                case QUOTA_EXCEEDED:
                    log.warn("FCM token quota exceeded", e);
                case UNAVAILABLE:
                    // retry nếu có queue
                    log.warn("FCM temporary error, should retry", e);
                    break;

                default:
                    log.error("FCM send failed", e);
            }
    }

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
            log.error("FCM send to token failed,  token={}", token);
            handleFirebaseMessagingException(e, token);
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
                        "Sent multicast: success={}, failure={}",
                        response.getSuccessCount(),
                        response.getFailureCount()
                );

            } catch (FirebaseMessagingException e) {
                log.error("FCM send multicast failed");
                handleFirebaseMessagingException(e, null);

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
            log.error("FCM subscribe failed, topic={} to token={}", topic, token);
            handleFirebaseMessagingException(e, null);
        }
    }

    @Override
    public void unsubscribeFromTopic(String token, String topic) {
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(List.of(token), topic);
            log.info("Unsubscribe to topic={} to token={}", topic, token);
        } catch (FirebaseMessagingException e) {
            log.error("FCM unsubscribe failed, topic={} to token={}", topic, token);
            handleFirebaseMessagingException(e, null);
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
            log.error("FCM send to topic failed, topic={}", topic);
            handleFirebaseMessagingException(e, null);
        }
    }
}

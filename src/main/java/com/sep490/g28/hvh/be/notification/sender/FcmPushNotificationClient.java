package com.sep490.g28.hvh.be.notification.sender;

import com.google.firebase.messaging.*;
import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;
import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FcmPushNotificationClient implements PushNotificationClient {
    NotificationTokenRepository tokenRepository;

    // FCM limit 500 token / request
    private static final int BATCH_SIZE = 500;

    @Transactional
    public void handleFirebaseMessagingException(FirebaseMessagingException e, String token) {
        MessagingErrorCode code = e.getMessagingErrorCode();

            switch (code) {
                // token dead
                case UNREGISTERED, SENDER_ID_MISMATCH:
                    if (token != null && !token.isBlank()) {
                        tokenRepository.deleteByToken(token);
                        log.warn("FCM token removed: {} ({})", token, code);
                    }
                    break;

                // invalid token/payload
                case INVALID_ARGUMENT:
                    if (token != null && !token.isBlank()) {
                        tokenRepository.deleteByToken(token);
                        log.warn("Invalid FCM token/payload, removed: {}", token);
                    }
                    break;

                // invalid auth/config
                case THIRD_PARTY_AUTH_ERROR:
                    log.error("FCM auth/config error (service account, senderId)", e);
                    break;

                // temporal error
                case UNAVAILABLE, INTERNAL:
                    log.warn("FCM temporary error, should retry: {}", code);
                    break;

                // rate/quota
                case QUOTA_EXCEEDED:
                    log.warn("FCM quota exceeded, backoff required");
                    break;

                default:
                    log.error("Unhandled FCM error code: {}", code, e);
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

    @Transactional
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

                if (response.getFailureCount() > 0) {
                    List<String> invalidTokens = getInvalidTokens(tokens, response);
                    tokenRepository.deleteByTokenIn(invalidTokens);
                }

            } catch (FirebaseMessagingException e) {
                log.error("FCM send multicast failed");
                handleFirebaseMessagingException(e, null);
            }
        }
    }

    private static List<String> getInvalidTokens(List<String> tokens, BatchResponse response) {
        List<String> invalidTokens = new ArrayList<>();

        for (int i = 0; i < response.getResponses().size(); i++) {
            SendResponse r = response.getResponses().get(i);
            if (!r.isSuccessful()) {
                String errorCode = r.getException().getMessagingErrorCode().name();
                if (errorCode.equals("UNREGISTERED")
                        || errorCode.equals("INVALID_ARGUMENT")
                        || errorCode.equals("SENDER_ID_MISMATCH")) {
                    invalidTokens.add(tokens.get(i));
                }
            }
        }
        return invalidTokens;
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

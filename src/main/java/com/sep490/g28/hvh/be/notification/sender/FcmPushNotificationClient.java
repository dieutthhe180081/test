package com.sep490.g28.hvh.be.notification.sender;

import com.google.firebase.messaging.*;
import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;
import com.sep490.g28.hvh.be.notification.service.NotificationTokenTxService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Firebase Cloud Messaging (FCM) implementation of {@link PushNotificationClient}.
 *
 * <p>Handles sending notifications to tokens, multicast batches,
 * and topics using the Firebase Admin SDK.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Respect FCM batch size limits (500 tokens/request)</li>
 *   <li>Handle and classify {@link FirebaseMessagingException}</li>
 *   <li>Clean up invalid or expired FCM tokens from persistence</li>
 *   <li>Support async delivery for single-token messages</li>
 * </ul>
 */
@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FcmPushNotificationClient implements PushNotificationClient {

    NotificationTokenTxService notificationTokenTxService;
    /** FCM maximum number of tokens per multicast request. */
    private static final int BATCH_SIZE = 500;

    /**
     * Centralized handler for Firebase messaging exceptions.
     *
     * <p>Depending on the error code, this method may:</p>
     * <ul>
     *   <li>Remove invalid/unregistered tokens from the database</li>
     *   <li>Log configuration or authentication issues</li>
     *   <li>Indicate retryable or quota-related errors</li>
     * </ul>
     *
     * @param e     Firebase messaging exception
     * @param token related device token (nullable)
     */
    public void handleFirebaseMessagingException(FirebaseMessagingException e, String token) {
        MessagingErrorCode code = e.getMessagingErrorCode();

            switch (code) {
                // token dead
                case UNREGISTERED, SENDER_ID_MISMATCH:
                    if (token != null && !token.isBlank()) {
                        notificationTokenTxService.deleteToken(token);
                        log.warn("FCM token removed: {} ({})", token, code);
                    }
                    break;

                // invalid token/payload
                case INVALID_ARGUMENT:
                    if (token != null && !token.isBlank()) {
                        notificationTokenTxService.deleteToken(token);
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

    /**
     * Asynchronously send a notification to a single device token.
     *
     * <p>Uses FCM {@link Message} with both notification and data payload.</p>
     *
     * @param token   target device token
     * @param payload notification content and custom data
     */
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
            //send message
            String msgId = FirebaseMessaging.getInstance().send(msg);
            log.info("Send message to token={} id={}", token, msgId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM send to token failed,  token={}", token);
            handleFirebaseMessagingException(e, token);
        }
    }

    /**
     * Send a notification to multiple device tokens asynchronously.
     *
     * <p>Splits the token list into batches of {@code BATCH_SIZE} to comply with
     * Firebase Cloud Messaging limits (max 500 tokens per request).</p>
     *
     * <p>After sending, failed responses are inspected and permanently invalid
     * tokens (UNREGISTERED, INVALID_ARGUMENT, SENDER_ID_MISMATCH) are removed
     * from the database.</p>
     *
     * @param tokens  list of target device tokens
     * @param payload notification content and custom data
     */
    @Async("pushExecutor")
    @Override
    public void sendMulticast(List<String> tokens, NotificationPayload payload) {
        //check tokens list
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        //split into batches
        for (int i = 0; i < tokens.size(); i += BATCH_SIZE) {
            List<String> batch =
                    tokens.subList(i, Math.min(i + BATCH_SIZE, tokens.size()));
            //create batch message
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
                //send batch message
                BatchResponse response =
                        FirebaseMessaging.getInstance().sendEachForMulticast(message);

                log.info(
                        "Sent multicast: success={}, failure={}",
                        response.getSuccessCount(),
                        response.getFailureCount()
                );

                //has some messages sent fail, delete respective tokens
                if (response.getFailureCount() > 0) {
                    List<String> invalidTokens = getInvalidTokens(tokens, response);
                    notificationTokenTxService.deleteInvalidTokens(invalidTokens);
                    log.warn("Delete invalid tokens: {}", invalidTokens);
                }

            } catch (FirebaseMessagingException e) {
                log.error("FCM send multicast failed");
                handleFirebaseMessagingException(e, null);
            }
        }
    }

    /**
     * Extract permanently invalid FCM tokens from a multicast response.
     *
     * <p>Only non-retryable errors are considered invalid and eligible
     * for removal.</p>
     *
     * @param tokens   original token list (same order as request)
     * @param response Firebase batch response
     * @return list of invalid tokens
     */
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

    /**
     * Asynchronously subscribe a device token to a Firebase topic.
     *
     * @param token device token
     * @param topic topic name
     */
    @Override
    @Async("pushExecutor")
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

    /**
     * Asynchronously unsubscribe a device token from a Firebase topic.
     *
     * @param token device token
     * @param topic topic name
     */
    @Override
    @Async("pushExecutor")
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

    /**
     * Asynchronously send a notification to all devices subscribed to a topic.
     *
     * @param topic   topic name
     * @param payload notification content and custom data
     */
    @Override
    @Async("pushExecutor")
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

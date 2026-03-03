package com.sep490.g28.hvh.be.notification.sender;

import com.google.firebase.messaging.*;
import com.sep490.g28.hvh.be.notification.constant.EFcmFailureType;
import com.sep490.g28.hvh.be.notification.exception.NonRetryableFcmException;
import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import com.sep490.g28.hvh.be.notification.service.NotificationTokenTxService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Firebase Cloud Messaging (FCM) implementation of {@link PushNotificationSender}.
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
public class FcmPushNotificationSender implements PushNotificationSender {

    NotificationTokenTxService notificationTokenTxService;
    /** FCM maximum number of tokens per multicast request. */
    private static final int BATCH_SIZE = 500;
    private final NotificationTokenRepository notificationTokenRepository;

    public EFcmFailureType classifyFcmFailureType(FirebaseMessagingException e, String token) {

        MessagingErrorCode code = e.getMessagingErrorCode();

        return switch (code) {
            //token dead, invalid token/payload
            case UNREGISTERED, SENDER_ID_MISMATCH, INVALID_ARGUMENT -> {
                if (token != null && !token.isBlank()) {
                    notificationTokenTxService.deleteToken(token);
                }
                yield EFcmFailureType.NON_RETRYABLE;
            }
            // temporal error, rate/quota
            case UNAVAILABLE, INTERNAL, QUOTA_EXCEEDED -> EFcmFailureType.RETRYABLE;
            // invalid auth/config
            case THIRD_PARTY_AUTH_ERROR -> {
                log.error("FCM auth/config error");
                yield EFcmFailureType.NON_RETRYABLE;
            }
        };
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
     * @param notification notification content and custom data
     */
    @Override
    @Async("pushExecutor")
    public void sendMulticast(com.sep490.g28.hvh.be.notification.entity.Notification notification) {
        List<String> tokens = notificationTokenRepository.findTokensByUserId(notification.getUser().getId());
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
                                    .setTitle(notification.getTitle())
                                    .setBody(notification.getBody())
                                    .build()
                    )
                    .putAllData(
                            notification.getData() == null ? Map.of() : notification.getData()
                    )
                    .build();

            try {
                //send batch message
                BatchResponse response =
                        FirebaseMessaging.getInstance().sendEachForMulticast(message);

                log.info(
                        "Sent multicast: notificationId={}, success={}, failure={}",
                        notification.getId(),
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
                log.error("FCM send multicast failed: notificationId={}", notification.getId(), e);
                EFcmFailureType type = classifyFcmFailureType(e, null);

                if (type == EFcmFailureType.RETRYABLE) {
                    throw new RuntimeException("FCM_RETRYABLE");
                } else {
                    throw new NonRetryableFcmException("FCM_NON_RETRYABLE");
                }
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
     * Asynchronously send a notification to all devices subscribed to a topic.
     *
     * @param notification notification content and custom data
     */
    @Override
    @Async("pushExecutor")
    public void sendToTopic(com.sep490.g28.hvh.be.notification.entity.Notification notification) {
        Message msg = Message.builder()
                .setTopic(notification.getTopic())
                .setNotification(
                        Notification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getBody())
                                .build()
                )
                .putAllData(
                        notification.getData() == null ? Map.of() : notification.getData()
                )
                .build();
        try {
            String msgId = FirebaseMessaging.getInstance().send(msg);
            log.info("Send message to topic={}, notificationId={}, msgId={}",
                    notification.getTopic(),
                    notification.getId(),
                    msgId
            );
        } catch (FirebaseMessagingException e) {
            log.error("FCM send to topic failed: topic={}, notificationId={}",
                    notification.getId(),
                    notification.getTopic(),
                    e
            );
            EFcmFailureType type = classifyFcmFailureType(e, null);

            if (type == EFcmFailureType.RETRYABLE) {
                throw new RuntimeException("FCM_RETRYABLE");
            } else {
                throw new NonRetryableFcmException("FCM_NON_RETRYABLE");
            }
        }
    }

    @Override
    @Async("pushExecutor")
    public void subscribeToTopics(String token, Collection<String> topics) {
        if (token == null || topics == null || topics.isEmpty()) return;

        try {
            for (String topic : topics) {
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(List.of(token), topic);
            }
            log.info("Subscribed token={} to topics={}", token, topics);
        } catch (FirebaseMessagingException e) {
            log.error("FCM subscribe failed token={} topics={}", token, topics, e);
            EFcmFailureType type = classifyFcmFailureType(e, null);

            if (type == EFcmFailureType.RETRYABLE) {
                throw new RuntimeException("FCM_RETRYABLE");
            } else {
                throw new NonRetryableFcmException("FCM_NON_RETRYABLE");
            }
        }
    }

    @Override
    @Async("pushExecutor")
    public void unsubscribeFromTopics(String token, Collection<String> topics) {
        if (token == null || topics == null || topics.isEmpty()) return;

        try {
            for (String topic : topics) {
                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(List.of(token), topic);
            }
            log.info("Unsubscribed token={} from topics={}", token, topics);
        } catch (FirebaseMessagingException e) {
            log.error("FCM unsubscribe failed token={} topics={}", token, topics, e);
            EFcmFailureType type = classifyFcmFailureType(e, null);

            if (type == EFcmFailureType.RETRYABLE) {
                throw new RuntimeException("FCM_RETRYABLE");
            } else {
                throw new NonRetryableFcmException("FCM_NON_RETRYABLE");
            }
        }
    }
}

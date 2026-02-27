package com.sep490.g28.hvh.be.notification.sender;

import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;

import java.util.List;

/**
 * Client abstraction for sending push notifications.
 *
 * <p>Implementations are responsible for handling provider-specific
 * details (e.g. Firebase Cloud Messaging).</p>
 */
public interface PushNotificationClient {

    /**
     * Send a notification to a single device token.
     *
     * @param token   target FCM device token
     * @param payload notification content and custom data
     */
    void sendToToken(String token, NotificationPayload payload);

    /**
     * Send a notification to multiple device tokens.
     *
     * <p>Implementations must handle provider limits
     * (e.g. FCM max 500 tokens per request).</p>
     *
     * @param tokens  list of target device tokens
     * @param payload notification content and custom data
     */
    void sendMulticast(List<String> tokens, NotificationPayload payload);

    /**
     * Subscribe a device token to a topic.
     *
     * @param token device token
     * @param topic topic name
     */
    void subscribeToTopic(String token, String topic);

    /**
     * Unsubscribe a device token from a topic.
     *
     * @param token device token
     * @param topic topic name
     */
    void unsubscribeFromTopic(String token, String topic);

    /**
     * Send a notification to all devices subscribed to a topic.
     *
     * @param topic   topic name
     * @param payload notification content and custom data
     */
    void sendToTopic(String topic, NotificationPayload payload);

}

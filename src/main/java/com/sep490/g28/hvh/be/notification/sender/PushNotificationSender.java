package com.sep490.g28.hvh.be.notification.sender;

import com.sep490.g28.hvh.be.notification.dto.PushNotificationPayload;

public interface PushNotificationSender {
//
//    /**
//     * Send push notification to list of device token
//     */
//    void sendToTokens(List<String> tokens, PushPayload payload);
//
//    /**
//     * Broadcast
//     */
//    void broadcast(PushPayload payload);
//
//    void sendToTopic(String topic, PushPayload payload);

    void sendToToken(String token, PushNotificationPayload payload);
}

package com.sep490.g28.hvh.be.notification.sender;

import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;

import java.util.List;

public interface PushNotificationClient {

    void sendToToken(String token, NotificationPayload payload);

    void sendMulticast(List<String> tokens, NotificationPayload payload);

    void subscribeToTopic(String token, String topic);

    void unsubscribeFromTopic(String token, String topic);

    void sendToTopic(String topic, NotificationPayload payload);

}

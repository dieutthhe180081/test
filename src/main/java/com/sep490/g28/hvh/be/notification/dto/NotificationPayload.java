package com.sep490.g28.hvh.be.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Payload model for sending notifications to Firebase Cloud Messaging (FCM).
 */
@Getter
@Builder
public class NotificationPayload {
    String title;
    String body;
    Map<String, String> data;
}

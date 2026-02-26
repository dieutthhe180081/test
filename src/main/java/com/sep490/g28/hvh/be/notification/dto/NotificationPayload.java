package com.sep490.g28.hvh.be.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * payload to send to firebase
 */
@Getter
@Builder
public class NotificationPayload {
    String title;
    String body;
    Map<String, String> data;
}

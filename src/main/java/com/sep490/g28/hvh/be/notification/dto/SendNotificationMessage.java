package com.sep490.g28.hvh.be.notification.dto;

import com.sep490.g28.hvh.be.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class SendNotificationMessage {
    private UUID notificationId;
    private UUID userId;
    private String topic;
    private String title;
    private String body;
    private Map<String, String> data;
}

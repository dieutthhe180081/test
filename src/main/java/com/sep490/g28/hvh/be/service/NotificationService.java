package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.notification.dto.RegisterNotificationTokenRequest;

import java.util.UUID;

public interface NotificationService {

    void registerNotificationToken(RegisterNotificationTokenRequest request);

    void unregisterNotificationToken(String token);

    void sendVerifyEventByOrgManagerNotification(Event event, boolean approved);
}

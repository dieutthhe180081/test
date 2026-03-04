package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.notification.dto.RegisterNotificationTokenRequest;

public interface NotificationService {

    void registerNotificationToken(RegisterNotificationTokenRequest request);

    void unregisterNotificationToken(String token);

//    List<UserNotification> getLatestNotification(OffsetDateTime cursor);

    void sendVerifyEventByOrgManagerNotification(Event event, boolean approved);
}

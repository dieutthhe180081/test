package com.sep490.g28.hvh.be.service;

import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.Host;
import com.sep490.g28.hvh.be.entity.OrganizationManager;
import com.sep490.g28.hvh.be.notification.dto.RegisterNotificationTokenRequest;

public interface NotificationService {

    void registerNotificationToken(RegisterNotificationTokenRequest request);

    void unregisterNotificationToken(String token);
//todo
//    List<UserNotification> getLatestNotification(OffsetDateTime cursor);

    void sendEventCreatedNotification(Event event, Host host);
    void sendVerifyEventByOrgManagerNotification(Event event, boolean approved);
}

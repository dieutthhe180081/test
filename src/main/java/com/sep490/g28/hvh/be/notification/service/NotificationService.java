package com.sep490.g28.hvh.be.notification.service;

import com.sep490.g28.hvh.be.notification.dto.RegisterTokenRequest;

public interface NotificationService {

    void registerNotificationToken(RegisterTokenRequest request);
}

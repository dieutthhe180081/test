package com.sep490.g28.hvh.be.notification.service;

public interface NotificationService {

    void sendToToken(String token, String title, String body);
}

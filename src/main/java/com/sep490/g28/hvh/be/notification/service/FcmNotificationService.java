package com.sep490.g28.hvh.be.notification.service;

import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;
//import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import com.sep490.g28.hvh.be.notification.dto.RegisterTokenRequest;
import com.sep490.g28.hvh.be.notification.sender.PushNotificationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService implements NotificationService {
//    private final NotificationTokenRepository tokenRepo;
    private final PushNotificationClient pushNotificationClient;

    @Override
    public void registerNotificationToken(RegisterTokenRequest request) {
        // todo, có cách nào để check token hợp lệ với fcm hoặc validate nó trươcs không nhỉ
    }
}

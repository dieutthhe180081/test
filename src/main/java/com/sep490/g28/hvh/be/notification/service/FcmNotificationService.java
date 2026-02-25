package com.sep490.g28.hvh.be.notification.service;

import com.sep490.g28.hvh.be.notification.dto.PushNotificationPayload;
//import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import com.sep490.g28.hvh.be.notification.sender.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService implements NotificationService {
//    private final NotificationTokenRepository tokenRepo;
    private final PushNotificationSender pushNotificationSender;


    @Override
    public void sendToToken(String token, String title, String body) {

        PushNotificationPayload payload = PushNotificationPayload.builder()
                .title(title)
                .body(body)
                .build();

        pushNotificationSender.sendToToken(token, payload);

    }
}

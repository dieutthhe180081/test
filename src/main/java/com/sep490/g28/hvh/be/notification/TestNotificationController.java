package com.sep490.g28.hvh.be.notification;

import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;
import com.sep490.g28.hvh.be.notification.dto.SendPushRequest;
import com.sep490.g28.hvh.be.notification.sender.PushNotificationClient;
import com.sep490.g28.hvh.be.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Test controller for notification
 */
@RestController
@RequestMapping("/test/notification")
@RequiredArgsConstructor
public class TestNotificationController {

    //todo delete this class after finish
    private final PushNotificationClient pushNotificationClient;
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody SendPushRequest request) {
        NotificationPayload payload = NotificationPayload.builder()
                .body(request.getBody())
                .title(request.getTitle())
                .build();
        pushNotificationClient.sendToToken(request.getToken(), payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-multicast")
    public ResponseEntity<Void> sendMulticast(@RequestBody SendPushRequest request) {
        NotificationPayload payload = NotificationPayload.builder()
                .body(request.getBody())
                .title(request.getTitle())
                .build();
        pushNotificationClient.sendMulticast(request.getTokens(), payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscribe-topic")
    public ResponseEntity<Void> subscribeTopic(
            @RequestParam String token,
            @RequestParam String topic
            ) {
        pushNotificationClient.subscribeToTopic(token, topic);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unsubscribe-topic")
    public ResponseEntity<Void> unsubscribeTopic(
            @RequestParam String token,
            @RequestParam String topic
    ) {
        pushNotificationClient.unsubscribeFromTopic(token, topic);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-topic")
    public ResponseEntity<Void> send(
            @RequestParam String topic,
            @RequestBody SendPushRequest request
    ) {
        NotificationPayload payload = NotificationPayload.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .build();
        pushNotificationClient.sendToTopic(topic, payload);
        return ResponseEntity.ok().build();
    }
}

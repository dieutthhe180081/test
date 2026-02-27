package com.sep490.g28.hvh.be.controller;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.notification.dto.RegisterNotificationTokenRequest;
import com.sep490.g28.hvh.be.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for notification
 */
@RestController
@RequestMapping("/api/v1/notification")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NotificationController {

    NotificationService notificationService;

    @PostMapping("/register-token")
    public ResponseEntity<Void> registerToken(@RequestBody RegisterNotificationTokenRequest request) {
        notificationService.registerNotificationToken(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/unregister-token")
    public ResponseEntity<Void> unregisterToken(@RequestParam String token) {
        notificationService.unregisterNotificationToken(token);
        return ResponseEntity.ok().build();
    }

}
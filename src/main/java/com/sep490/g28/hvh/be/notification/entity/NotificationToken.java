package com.sep490.g28.hvh.be.notification.entity;

import com.sep490.g28.hvh.be.constant.EPlatform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Notification token of user and device save in db
 */
@Entity
@Table(name = "notification_token")
@Getter
@Setter
public class NotificationToken {

    @Id
    @GeneratedValue
    UUID id;

    UUID userId;

    @Column(nullable = false, unique = true)
    String token;

    @Enumerated(EnumType.STRING)
    EPlatform platform; // WEB, ANDROID, IOS

    @Column(name = "device_id", nullable = false)
    String deviceId;
}

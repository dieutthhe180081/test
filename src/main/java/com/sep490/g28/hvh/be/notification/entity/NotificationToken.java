package com.sep490.g28.hvh.be.notification.entity;

import com.sep490.g28.hvh.be.constant.EPlatform;
import com.sep490.g28.hvh.be.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Notification token of user and device save in db
 */
@Entity
@Table(name = "notification_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationToken {

    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @Column(nullable = false, unique = true)
    String token;

    @Enumerated(EnumType.STRING)
    EPlatform platform; // WEB, ANDROID, IOS

    @Column(name = "device_id", nullable = false)
    String deviceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    OffsetDateTime createdAt;
}

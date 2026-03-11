package com.sep490.g28.hvh.be.notification.entity;

import com.sep490.g28.hvh.be.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The subscription of user to topics
 */
@Entity
@Table(
        name = "notification_topic_subscriptions",
        indexes = {
        @Index(name = "idx_notification_topic_subscription_user", columnList = "user_id"),
        }
    )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTopicSubscription {
    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @Column(nullable = false)
    String topic;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    OffsetDateTime createdAt;

}

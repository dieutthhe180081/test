package com.sep490.g28.hvh.be.notification.entity;

import com.sep490.g28.hvh.be.constant.ENotificationType;
import com.sep490.g28.hvh.be.entity.User;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * The notification sent
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    String topic;

    @Enumerated(EnumType.STRING)
    ENotificationType type;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String body;

    @Type(JsonType.class)   // Hibernate 6
    @Column(columnDefinition = "jsonb")
    Map<String, String> data;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    OffsetDateTime createdAt;
}

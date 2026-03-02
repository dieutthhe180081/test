package com.sep490.g28.hvh.be.notification.repository;

import com.sep490.g28.hvh.be.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}

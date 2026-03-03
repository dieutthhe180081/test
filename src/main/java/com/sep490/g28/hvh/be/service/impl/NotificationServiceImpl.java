package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.ENotificationType;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.User;
import com.sep490.g28.hvh.be.notification.entity.Notification;
import com.sep490.g28.hvh.be.notification.entity.NotificationToken;
import com.sep490.g28.hvh.be.notification.messageque.NotificationPublisher;
import com.sep490.g28.hvh.be.notification.repository.NotificationRepository;
import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import com.sep490.g28.hvh.be.notification.dto.RegisterNotificationTokenRequest;
import com.sep490.g28.hvh.be.notification.repository.NotificationTopicSubscriptionRepository;
import com.sep490.g28.hvh.be.notification.service.NotificationTokenTxService;
import com.sep490.g28.hvh.be.repository.UserRepository;
import com.sep490.g28.hvh.be.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final UserRepository userRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTopicSubscriptionRepository notificationTopicSubscriptionRepository;

    private final CurrentUserProvider currentUserProvider;

    private final NotificationTokenTxService notificationTokenTxService;

    private final NotificationPublisher notificationPublisher;

    private static final String ORG_TOPIC_PRE = "org_"; //org_{orgId}
    private static final String EVENT_TOPIC_PRE = "event_"; //event_{eventId}
    private static final String ADMIN_TOPIC = "admin";

    private static final String DATA_REF_ID_KEY = "refId";
    private static final String DATA_NOTIFICATION_TYPE = "type";

    @Override
    public void registerNotificationToken(RegisterNotificationTokenRequest request) {
        UUID userId = currentUserProvider.getId();
        NotificationToken notificationToken = registerNotificationTokenInternal(request, userId);

        //subscribe token to topic
        subscribeTokenToTopicsAfterRegister(notificationToken.getToken(), userId);
    }

    public NotificationToken registerNotificationTokenInternal(RegisterNotificationTokenRequest request, UUID userId) {
        User user = userRepository.getReferenceById(userId);

        NotificationToken tokenEntity =
                notificationTokenRepository
                        .findByUserIdAndPlatformAndDeviceId(
                                user.getId(),
                                request.getPlatform().name(),
                                request.getDeviceId()
                        )
                        .orElseGet(NotificationToken::new);

        tokenEntity.setUser(user);
        tokenEntity.setPlatform(request.getPlatform());
        tokenEntity.setToken(request.getToken());
        tokenEntity.setDeviceId(request.getDeviceId());

        notificationTokenRepository.save(tokenEntity);
        log.info("Registered notification token: {}", tokenEntity);

        return tokenEntity;
    }

    private void subscribeTokenToTopicsAfterRegister(String token, UUID userId) {
        List<String> topics = notificationTopicSubscriptionRepository.findTopicsByUserId(userId);

        notificationPublisher.enqueueSubscribeToTopics(token, topics);
    }

    @Override
    public void unregisterNotificationToken(String token) {
        UUID userId = currentUserProvider.getId();

        unregisterNotificationTokenInternal(token);

        unsubscribeTopicsAfterUnregister(token, userId);

    }

    public void unregisterNotificationTokenInternal(String token) {
        notificationTokenTxService.deleteToken(token);
        log.info("Unregistered notification token: {}", token);
    }

    private void unsubscribeTopicsAfterUnregister(String token, UUID userId) {
        List<String> topics = notificationTopicSubscriptionRepository.findTopicsByUserId(userId);

        notificationPublisher.enqueueUnsubscribeFromTopics(token, topics);
    }

    @Override
    public void sendVerifyEventByOrgManagerNotification(Event event, boolean approved) {
        Notification notification = new Notification();
        User user = userRepository.getReferenceById(event.getHost().getId());
        notification.setUser(user);

        if (approved) {
            notification.setTitle("Sự kiện đã được Quản lí tổ chức phê duyệt");
            notification.setBody(String.format("Sự kiện %s đã được phê duyệt và bước vào trạng thái tuyển người.", event.getName()));
            notification.setData(Map.of(
                    DATA_NOTIFICATION_TYPE, ENotificationType.EVENT_APPROVED_BY_MNG.name(),
                    DATA_REF_ID_KEY, event.getId().toString()
            ));
            notification.setType(ENotificationType.EVENT_APPROVED_BY_MNG);
        } else {
            notification.setTitle("Sự kiện không được chấp thuận bởi Quản lí tổ chức");
            notification.setBody(String.format("Quản lí tổ chức đã không chấp thuận tạo sự kiện %s.", event.getName()));
            notification.setData(Map.of(
                    DATA_NOTIFICATION_TYPE, ENotificationType.EVENT_REJECTED_BY_MNG.name(),
                    DATA_REF_ID_KEY, event.getId().toString()
            ));
            notification.setType(ENotificationType.EVENT_REJECTED_BY_MNG);
        }

        notificationRepository.save(notification);

        notificationPublisher.enqueueNotification(notification);
    }
}

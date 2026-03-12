package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.ENotificationDataAction;
import com.sep490.g28.hvh.be.constant.ENotificationType;
import com.sep490.g28.hvh.be.entity.Event;
import com.sep490.g28.hvh.be.entity.Host;
import com.sep490.g28.hvh.be.entity.User;
import com.sep490.g28.hvh.be.notification.entity.Notification;
import com.sep490.g28.hvh.be.notification.entity.UserNotification;
import com.sep490.g28.hvh.be.notification.entity.NotificationToken;
import com.sep490.g28.hvh.be.notification.messageque.NotificationPublisher;
import com.sep490.g28.hvh.be.notification.repository.UserNotificationRepository;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final UserNotificationRepository userNotificationRepository;

    private final CurrentUserProvider currentUserProvider;

    private final NotificationTokenTxService notificationTokenTxService;

    private final NotificationPublisher notificationPublisher;

    private static final String ORG_TOPIC_PRE = "org_"; //org_{orgId}
    private static final String EVENT_TOPIC_PRE = "event_"; //event_{eventId}
    private static final String ADMIN_TOPIC = "admin";

    private static final String DATA_REF_ID_KEY = "refId";
    private static final String DATA_ACTION = "action";
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

        //todo subscribe the to topic admin if the user is admin
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

    //    @Override
//    public List<UserNotification> getLatestNotification(OffsetDateTime cursor) {
//        UUID currentUserId = currentUserProvider.getId();
//        Pageable pageable = PageRequest.of(0, 20);
//
//        if (cursor == null) {
//            return userNotificationRepository.findFirstPage(currentUserId, pageable);
//        }
//
//        return userNotificationRepository.findNextPage(currentUserId, cursor, pageable);
//    }

    //only used for send notification to user
    private Notification saveNotificationForUser(Notification notification, User user) {
        //save notification
        notification = notificationRepository.save(notification);

        //link the notification to user in the db
        UserNotification userNotification = new UserNotification();
        userNotification.setNotification(notification);
        userNotification.setUser(user);

        userNotificationRepository.save(userNotification);
        return notification;
    }

    @Override
    public void sendEventCreatedNotification(Event event, Host host) {
        Notification notification = new Notification();
        User orgManager = userRepository.getReferenceById(host.getCreatedBy().getId());

        notification.setType(ENotificationType.MNG_EVENT_CREATED);
        notification.setTitle("Sự kiện mới được tạo");
        notification.setBody(String.format("Sự kiện \"%s\" vừa được tạo và cần xác nhận.", event.getName()));
        notification.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.MNG_EVENT_CREATED.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.MNG_EVENT_DETAILS.name()
        ));

        notification = saveNotificationForUser(notification, orgManager);

        notificationPublisher.enqueueNotification(notification, orgManager.getId());
    }

    @Override
    @Transactional
    public void sendEventApprovedByOrgManagerNotification(Event event) {
        //send notification to host
        Notification notificationForHost = new Notification();
        User host = userRepository.getReferenceById(event.getHost().getId());

        notificationForHost.setTitle("Sự kiện đã được Quản lí tổ chức phê duyệt");
        notificationForHost.setBody(String.format("Sự kiện %s đã được phê duyệt bởi quản lí tổ chức và đang chờ duyệt từ Admin.", event.getName()));
        notificationForHost.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.HOST_EVENT_APPROVED_BY_MNG.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.HOST_EVENT_DETAILS.name()
        ));
        notificationForHost.setType(ENotificationType.HOST_EVENT_APPROVED_BY_MNG);

        //save notification
        notificationForHost = saveNotificationForUser(notificationForHost, host);

        //send notification to admin
        Notification notificationForAdmin = new Notification();
        notificationForAdmin.setTopic(ADMIN_TOPIC);
        notificationForAdmin.setTitle("Sự kiện đã được Quản lí tổ chức phê duyệt");
        notificationForAdmin.setBody(String.format("Sự kiện %s đã được phê duyệt bởi quản lí tổ chức và cần bạn xác nhận.", event.getName()));
        notificationForAdmin.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.ADM_EVENT_APPROVED_BY_MNG.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.ADM_EVENT_DETAILS.name()
        ));
        notificationForHost.setType(ENotificationType.ADM_EVENT_APPROVED_BY_MNG);
        notificationRepository.save(notificationForAdmin);

        //send notification
        notificationPublisher.enqueueNotification(notificationForHost, host.getId());
        notificationPublisher.enqueueNotification(notificationForAdmin, null);
    }

    @Override
    public void sendEventRejectedByOrgManagerNotification(Event event, String reason) {
        //send notification to host
        Notification notification = new Notification();
        User host = userRepository.getReferenceById(event.getHost().getId());

        notification.setTitle("Sự kiện không được chấp thuận bởi Quản lí tổ chức");
        notification.setBody(String.format("Quản lí tổ chức đã không chấp thuận tạo sự kiện %s. Lí do: %s", event.getName(), reason));
        notification.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.HOST_EVENT_REJECTED_BY_MNG.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.HOST_EVENT_DETAILS.name()
        ));
        notification.setType(ENotificationType.HOST_EVENT_REJECTED_BY_MNG);

        //save notification
        notification = saveNotificationForUser(notification, host);

        notificationPublisher.enqueueNotification(notification, host.getId());
    }

    @Override
    public void sendEventApprovedByAdminNotification(Event event) {
        //send notification to host
        Notification notificationForHost = new Notification();
        User host = userRepository.getReferenceById(event.getHost().getId());

        notificationForHost.setTitle("Sự kiện đã được Admin phê duyệt");
        notificationForHost.setBody(String.format("Sự kiện %s đã được phê duyệt bởi Admin và bước vào tranng thái tuyển người.", event.getName()));
        notificationForHost.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.HOST_EVENT_APPROVED_BY_AD.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.HOST_EVENT_DETAILS.name()
        ));
        notificationForHost.setType(ENotificationType.HOST_EVENT_APPROVED_BY_AD);

        //save notification
        notificationForHost = saveNotificationForUser(notificationForHost, host);

        //send notification to manager
        Notification notificationForManager = new Notification();
        User orgManager = userRepository.getReferenceById(event.getHost().getCreatedBy().getId());
        notificationForManager.setTitle("Sự kiện đã được Admin phê duyệt");
        notificationForManager.setBody(String.format("Sự kiện %s đã được phê duyệt bởi Admin và bước vào tranng thái tuyển người.", event.getName()));
        notificationForManager.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.MNG_EVENT_APPROVED_BY_AD.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.MNG_EVENT_DETAILS.name()
        ));
        notificationForManager.setType(ENotificationType.MNG_EVENT_APPROVED_BY_AD);

        //save notification
        notificationForHost = saveNotificationForUser(notificationForHost, orgManager);


        notificationPublisher.enqueueNotification(notificationForHost, host.getId());
        notificationPublisher.enqueueNotification(notificationForManager, orgManager.getId());
    }

    @Override
    public void sendEventRejectedByAdminNotification(Event event, String reason) {
        //send notification to host
        Notification notificationForHost = new Notification();
        User host = userRepository.getReferenceById(event.getHost().getId());

        notificationForHost.setTitle("Sự kiện không được chấp thuận bởi Admin");
        notificationForHost.setBody(String.format("Sự kiện %s đã không được chấp thuận bởi admin với lí do: %s.", event.getName(), reason));
        notificationForHost.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.HOST_EVENT_REJECTED_BY_AD.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.HOST_EVENT_DETAILS.name()
        ));
        notificationForHost.setType(ENotificationType.HOST_EVENT_REJECTED_BY_AD);

        //save notification
        notificationForHost = saveNotificationForUser(notificationForHost, host);

        //send notification to manager
        Notification notificationForManager = new Notification();
        User orgManager = userRepository.getReferenceById(event.getHost().getCreatedBy().getId());
        notificationForManager.setTitle("Sự kiện không được chấp thuận bởi Admin");
        notificationForManager.setBody(String.format("Sự kiện %s đã không được chấp thuận bởi admin với lí do: %s.", event.getName(), reason));
        notificationForManager.setData(Map.of(
                DATA_NOTIFICATION_TYPE, ENotificationType.MNG_EVENT_REJECTED_BY_AD.name(),
                DATA_REF_ID_KEY, event.getId().toString(),
                DATA_ACTION, ENotificationDataAction.MNG_EVENT_DETAILS.name()
        ));
        notificationForManager.setType(ENotificationType.MNG_EVENT_REJECTED_BY_AD);

        //save notification
        notificationForHost = saveNotificationForUser(notificationForHost, orgManager);

        notificationPublisher.enqueueNotification(notificationForHost, host.getId());
        notificationPublisher.enqueueNotification(notificationForManager, orgManager.getId());
    }
}

package com.sep490.g28.hvh.be.service.impl;

import com.sep490.g28.hvh.be.auth.CurrentUserProvider;
import com.sep490.g28.hvh.be.constant.ERole;
import com.sep490.g28.hvh.be.notification.dto.NotificationPayload;
import com.sep490.g28.hvh.be.notification.entity.NotificationToken;
import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import com.sep490.g28.hvh.be.notification.dto.RegisterNotificationTokenRequest;
import com.sep490.g28.hvh.be.notification.sender.PushNotificationClient;
import com.sep490.g28.hvh.be.notification.service.NotificationTokenTxService;
import com.sep490.g28.hvh.be.repository.HostRepository;
import com.sep490.g28.hvh.be.repository.OrganizationManagerRepository;
import com.sep490.g28.hvh.be.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTokenRepository notificationTokenRepository;
    private final HostRepository hostRepository;
    private final OrganizationManagerRepository organizationManagerRepository;

    private final PushNotificationClient pushNotificationClient;
    private final CurrentUserProvider currentUserProvider;

    private final NotificationTokenTxService notificationTokenTxService;

    private static final String ORG_TOPIC_PRE = "org_"; //org_{orgId}
    private static final String EVENT_TOPIC_PRE = "event_"; //event_{eventId}
    private static final String ADMIN_TOPIC = "admin";

    @Override
    public void registerNotificationToken(RegisterNotificationTokenRequest request) {
        NotificationToken notificationToken = registerNotificationTokenInternal(request);

        //subscribe token to topic
        subscribeTokenToTopicsAfterRegister(notificationToken);
    }

    public NotificationToken registerNotificationTokenInternal(RegisterNotificationTokenRequest request) {
        UUID userId = currentUserProvider.getId();

        NotificationToken tokenEntity =
                notificationTokenRepository
                        .findByUserIdAndPlatformAndDeviceId(
                                userId,
                                request.getPlatform(),
                                request.getDeviceId()
                        )
                        .orElseGet(NotificationToken::new);

        tokenEntity.setUserId(userId);
        tokenEntity.setPlatform(request.getPlatform());
        tokenEntity.setToken(request.getToken());
        tokenEntity.setDeviceId(request.getDeviceId());

        notificationTokenRepository.save(tokenEntity);
        log.info("Registered notification token: {}", tokenEntity);

        return tokenEntity;
    }

    private void subscribeTokenToTopicsAfterRegister(NotificationToken tokenEntity) {
        UUID userId = currentUserProvider.getId();
        String fcmToken = tokenEntity.getToken();

        switch (currentUserProvider.getRoleName()) {
            case ERole.VOL -> {
                //todo find all the event the volunteer register in
            } case ERole.HOST -> {
                //subscribe the token to topic of org which the host is working for
                UUID orgId = hostRepository.findById(userId)
                        .orElseThrow()
                        .getOrganization()
                        .getId();
                pushNotificationClient.subscribeToTopic(fcmToken, ORG_TOPIC_PRE + orgId);

                //todo subscribe the token to topics of events which the host is hosting

            } case ERole.ORG_MANAGER -> {
                //subscribe the token to topic of manager's org
                UUID orgId = organizationManagerRepository.findById(userId)
                        .orElseThrow()
                        .getOrganization()
                        .getId();
                pushNotificationClient.subscribeToTopic(fcmToken, ORG_TOPIC_PRE + orgId);

            } case ERole.SYS_ADMIN ->
                //subscribe the token to admin 's topic
             pushNotificationClient.subscribeToTopic(fcmToken, ADMIN_TOPIC);
        }
    }

    @Override
    public void unregisterNotificationToken(String token) {
        UUID userId = currentUserProvider.getId();

        unregisterNotificationTokenInternal(token);

        unsubscribeTopicsAfterUnregister(userId, token);

    }

    private void unsubscribeTopicsAfterUnregister(UUID userId, String token) {
        //unsubscribe token to topic
        switch (currentUserProvider.getRoleName()) {
            case ERole.VOL -> {
                //todo find all the event the volunteer register in
            } case ERole.HOST -> {
                //unsubscribe the token to topic of org which the host is working for
                UUID orgId = hostRepository.findById(userId)
                        .orElseThrow()
                        .getOrganization()
                        .getId();
                pushNotificationClient.unsubscribeFromTopic(token, ORG_TOPIC_PRE + orgId);

                //todo unsubscribe the token to topics of events which the host is hosting

            } case ERole.ORG_MANAGER -> {
                //unsubscribe the token to topic of manager's org

                UUID orgId = organizationManagerRepository.findById(userId)
                        .orElseThrow()
                        .getOrganization()
                        .getId();
                pushNotificationClient.unsubscribeFromTopic(token, ORG_TOPIC_PRE + orgId);

            } case ERole.SYS_ADMIN ->
                //unsubscribe the token to admin 's topic
                    pushNotificationClient.unsubscribeFromTopic(token, ADMIN_TOPIC);
        }
    }

    public void unregisterNotificationTokenInternal(String token) {
        notificationTokenTxService.deleteToken(token);
        log.info("Unregistered notification token: {}", token);
    }

    @Override
    public void sendVerifyEventByOrgManagerNotification(String eventName, UUID hostId, boolean approved) {
        String title;
        String body;
        if (approved) {
            title = "Sự kiện đã được Quản lí tổ chức phê duyệt";
            body = String.format("Sự kiện %s đã được phê duyệt và bước vào trạng thái tuyển người.", eventName);
        } else {
            title = "Sự kiện không được chấp thuận bởi Quản lí tổ chức";
            body = String.format("Quản lí tổ chức đã không chấp thuận tạo sự kiện %s.", eventName);
        }

        NotificationPayload payload = NotificationPayload.builder()
                .title(title)
                .body(body)
                .build();

        List<String> tokens = notificationTokenRepository.findTokensByUserId(hostId);
        pushNotificationClient.sendMulticast(tokens, payload);
    }
}

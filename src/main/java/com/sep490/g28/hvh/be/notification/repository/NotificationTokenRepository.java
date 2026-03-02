package com.sep490.g28.hvh.be.notification.repository;

import com.sep490.g28.hvh.be.constant.EPlatform;
import com.sep490.g28.hvh.be.notification.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, UUID> {

    @Query(
            value = """
                      select *
                      from notification_tokens
                      where user_id = :userId
                        and platform = :platform
                        and device_id = :deviceId
                    """,
            nativeQuery = true
    )
    Optional<NotificationToken> findByUserIdAndPlatformAndDeviceId(UUID userId, EPlatform platform, String deviceId);

    @Query(
            value = """
                      select token
                      from notification_tokens
                      where user_id = :userId
                    """,
            nativeQuery = true
    )
    List<String> findTokensByUserId(@Param("userId") UUID userId);

    void deleteByToken(String token);

    void deleteByTokenIn(List<String> invalidTokens);
}

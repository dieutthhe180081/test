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

    Optional<NotificationToken> findByUserIdAndPlatformAndDeviceId(UUID userId, EPlatform platform, String deviceId);

    @Query("select nt.token from NotificationToken nt where nt.userId = :userId")
    List<String> findTokensByUserId(@Param("userId") UUID userId);

    void deleteByToken(String token);

    void deleteByTokenIn(List<String> invalidTokens);
}

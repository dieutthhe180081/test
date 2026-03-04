package com.sep490.g28.hvh.be.notification.service;

import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Transactional service for managing notification tokens.
 *
 * <p>Encapsulates token deletion logic to ensure database operations
 * are executed within a transactional boundary.</p>
 *
 * <p>Main use case: remove invalid or expired FCM tokens detected
 * during push notification delivery.</p>
 */
@Service
@RequiredArgsConstructor
public class NotificationTokenTxService {

    private final NotificationTokenRepository tokenRepository;

    /**
     * Delete multiple invalid device tokens.
     *
     * <p>No operation if the token list is null or empty.</p>
     *
     * @param tokens list of invalid device tokens
     */
    @Transactional
    public void deleteInvalidTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return;
        tokenRepository.deleteByTokenIn(tokens);
    }

    /**
     * Delete a single device token.
     *
     * @param token device token to remove
     */
    @Transactional
    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
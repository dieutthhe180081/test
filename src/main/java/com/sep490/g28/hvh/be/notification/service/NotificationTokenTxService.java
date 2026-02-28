package com.sep490.g28.hvh.be.notification.service;

import com.sep490.g28.hvh.be.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationTokenTxService {

    private final NotificationTokenRepository tokenRepository;

    @Transactional
    public void deleteInvalidTokens(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return;
        tokenRepository.deleteByTokenIn(tokens);
    }

    @Transactional
    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
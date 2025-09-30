package com.group_three.food_ordering.services;

import com.group_three.food_ordering.repositories.RefreshTokenRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.security.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.refresh-expiration}") // 7 días
    private long refreshTokenExpiration;


    public String generateRefreshToken(String userEmail) {
        // Revocar tokens existentes del usuario
        refreshTokenRepository.revokeAllByUserEmail(userEmail);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userEmail(userEmail)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        log.debug("[Refresh token Service] Generated refresh token for user={} expiresAt={}", refreshToken.getUserEmail(), Date.from(refreshToken.getExpiresAt()));
        return refreshToken.getToken();
    }

    public Optional<String> validateAndGetUserEmail(String token) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .filter(refreshToken -> refreshToken.getExpiresAt().isAfter(Instant.now()))
                .map(refreshToken -> {
                    // Marcar como usado
                    refreshToken.setUsedAt(Instant.now());
                    refreshTokenRepository.save(refreshToken);
                    return refreshToken.getUserEmail();
                });
    }

    public void revokeToken(String token) {
        refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
        log.debug("[Refresh token Service] Revoked refresh token");
    }

    public void revokeAllUserTokens(String userEmail) {
        refreshTokenRepository.revokeAllByUserEmail(userEmail);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Diario a las 2 AM
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}

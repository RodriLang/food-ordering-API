package com.group_three.food_ordering.configs.security;

import com.group_three.food_ordering.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    public String generateRefreshToken(String userEmail) {
        // Revocar tokens existentes del usuario
        log.debug("[RefreshTokenService] Revoke existing token for user={}", userEmail);
        log.debug("[RefreshTokenRepository] Calling revokeAllByUserEmail for user={}", userEmail);
        refreshTokenRepository.revokeAllByUserEmail(userEmail);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(passwordEncoder.encode(UUID.randomUUID().toString()))
                .userEmail(userEmail)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .createdAt(Instant.now())
                .revoked(false)
                .build();

        log.debug("[RefreshTokenRepository] Calling save to create new refresh token for user={}", userEmail);
        refreshTokenRepository.save(refreshToken);
        log.debug("[RefreshTokenService] Generated refresh token for user={}", userEmail);
        return refreshToken.getToken();
    }

    public Optional<String> validateAndGetUserEmail(String token) {
        log.debug("[RefreshTokenService] Validating refresh token");

        log.debug("[RefreshTokenRepository] Calling findByTokenAndRevokedFalse");
        return refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .filter(refreshToken -> refreshToken.getExpiresAt().isAfter(Instant.now()))
                .map(refreshToken -> {
                    // Marcar como usado
                    refreshToken.setUsedAt(Instant.now());
                    log.debug("[RefreshTokenRepository] Calling save to mark token as used");
                    refreshTokenRepository.save(refreshToken);
                    return refreshToken.getUserEmail();
                });
    }

    public void revokeToken(String token) {
        log.debug("[RefreshTokenRepository] Calling findByTokenAndRevokedFalse to check token for revocation");
        refreshTokenRepository.findByTokenAndRevokedFalse(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    log.debug("[RefreshTokenRepository] Calling save to revoke token");
                    refreshTokenRepository.save(refreshToken);
                });
        log.debug("[Refresh token Service] Revoked refresh token");
    }

    @Scheduled(cron = "0 0 2 * * ?") // Diario a las 2 AM
    public void cleanupExpiredTokens() {
        log.debug("[RefreshTokenService] Cleaning up expired tokens");
        log.debug("[RefreshTokenRepository] Calling deleteByExpiresAtBefore");
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}

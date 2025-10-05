package com.group_three.food_ordering.security;

import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.time.LocalTime.now;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret:myDefaultSecretKeyForJWTTokenGenerationThatShouldBeLongEnoughForSecurity}")
    private String jwtSecret;

    @Value("${jwt.access-expiration}")
    private long jwtAccessExpirationMs;

    private final UserRepository userRepository;

    public String generateAccessToken(SessionInfo sessionInfo) {
        Date expiration = Date.from(Instant.now().plusMillis(jwtAccessExpirationMs));

        Map<String, Object> claims = new HashMap<>();
        if (sessionInfo.foodVenueId() != null) claims.put("foodVenueId", sessionInfo.foodVenueId().toString());
        if (sessionInfo.tableSessionId() != null) claims.put("tableSessionId", sessionInfo.tableSessionId().toString());
        if (sessionInfo.participantId() != null) claims.put("participantId", sessionInfo.participantId().toString());
        if (sessionInfo.role() != null) claims.put("role", sessionInfo.role());

        String token = Jwts.builder()
                .subject(sessionInfo.subject())
                .claims(claims)
                .issuedAt(Date.from(Instant.now()))
                .expiration(expiration)
                .signWith(getSignatureKey())
                .compact();

        log.debug("[JwtService] Generated access token for user={} expiresAt={}", sessionInfo.subject(), expiration);
        return token;
    }

    public Claims parseTokenClaimsSafe(String token) throws JwtException {
        try {
            return Jwts.parser()
                    .verifyWith(getSignatureKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // Devuelve claims aunque el token esté expirado
        }
    }

    public Boolean isTokenValid(String token) {
        try {
            parseTokenClaimsSafe(token); // validación de firma
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[JwtService] Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = parseTokenClaimsSafe(token);
            Date expiration = claims.getExpiration();
            boolean expired = expiration.before(Date.from(Instant.now()));
            if (expired) {
                log.debug("[JwtService] Token is expired at {} current time={}", expiration, now());
            }
            return expired;
        } catch (JwtException e) {
            log.warn("[JwtService] Cannot check expiration, token invalid: {}", e.getMessage());
            return true;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = parseTokenClaimsSafe(token);
        return claimsTFunction.apply(claims);
    }

    public SessionInfo getSessionInfoFromToken(String token) {
        String subject = getUsernameFromToken(token);
        String role = getClaim(token, claims -> claims.get("role", String.class));
        String participantIdStr = getClaim(token, claims -> claims.get("participantId", String.class));
        String tableSessionIdStr = getClaim(token, claims -> claims.get("tableSessionId", String.class));
        String foodVenueIdStr = getClaim(token, claims -> claims.get("foodVenueId", String.class));

        UUID participantId = participantIdStr != null ? UUID.fromString(participantIdStr) : null;
        UUID tableSessionId = tableSessionIdStr != null ? UUID.fromString(tableSessionIdStr) : null;
        UUID foodVenueId = foodVenueIdStr != null ? UUID.fromString(foodVenueIdStr) : null;

        return new SessionInfo(subject, foodVenueId, role, participantId, tableSessionId);
    }

    private SecretKey getSignatureKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException e) {
            log.warn("[JwtService] jwtSecret is not base64 encoded - using raw UTF-8 bytes. Prefer base64-encoded 256-bit key.");
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret key too short: must be at least 256 bits (32 bytes)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Instant getExpirationDateFromToken(String token) {
        Date d = getClaim(token, Claims::getExpiration);
        return d == null ? null : d.toInstant();
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaim(token, Claims::getIssuedAt);
    }

}

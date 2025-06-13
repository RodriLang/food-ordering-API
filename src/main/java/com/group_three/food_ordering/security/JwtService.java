package com.group_three.food_ordering.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret:myDefaultSecretKeyForJWTTokenGenerationThatShouldBeLongEnoughForSecurity}")
    private String jwtSecret;

    @Value("${jwt.expirationMs:86400000}")
    private long jwtExpirationMs;

    public String generateToken(String email,
                                UUID foodVenueId,
                                String role,
                                UUID tableSessionId,
                                UUID clientId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("foodVenueId", foodVenueId);
        claims.put("tableSessionId", tableSessionId);
        claims.put("clientId", clientId);
        claims.put("role", role);

        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignatureKey())
                .compact();
    }

    // Validate access token
    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignatureKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Alternative validation method (same functionality, different name)
    public boolean validateToken(String token) {
        return isTokenValid(token);
    }

    // Check if token is expired
    public Boolean isTokenExpired(String token) {
        Date expiration = getClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Obtain username from token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // Obtain one specific claim
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Obtain all claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignatureKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getFoodVenueId(String token) {
        return getClaim(token, claims -> claims.get("foodVenueId", String.class));
    }

    // Alternative method for getting claims (same functionality)
    private Claims getClaims(String token) {
        return extractAllClaims(token);
    }

    // Obtain signature key
    private SecretKey getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Get token expiration time
    public Date getExpirationDateFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    // Get token issued date
    public Date getIssuedAtDateFromToken(String token) {
        return getClaim(token, Claims::getIssuedAt);
    }
}


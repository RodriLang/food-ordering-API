package com.group_three.food_ordering.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.enums.RoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // excluimos endpoints de autenticación que no requieren validación de access token
        return path.startsWith(ApiPaths.AUTH_URI + "/login")
                || path.startsWith(ApiPaths.AUTH_URI + "/register")
                || path.startsWith(ApiPaths.AUTH_URI + "/logout")
                || path.startsWith(ApiPaths.AUTH_URI + "/refresh")
                || path.startsWith(ApiPaths.TABLE_SESSION_URI + "/public/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            log.debug("[JwtAuthenticationFilter] Verifying token expiration");
            if (Boolean.TRUE.equals(jwtService.isTokenExpired(token))) {
                sendTokenExpiredError(response);
                return; // no seguimos
            }

            log.debug("[JwtAuthenticationFilter] Validating access token");
            if (!Boolean.TRUE.equals(jwtService.isTokenValid(token))) {
                sendUnauthorizedError(response, "INVALID_TOKEN", "JWT token is invalid");
                return;
            }

            log.debug("[JwtAuthenticationFilter] Extracting claims from token");
            String email = jwtService.getUsernameFromToken(token);
            String role = jwtService.getClaim(token, claims -> claims.get("role", String.class));
            String participantIdStr = jwtService.getClaim(token, claims -> claims.get("participantId", String.class));
            String tableSessionIdStr = jwtService.getClaim(token, claims -> claims.get("tableSessionId", String.class));
            String foodVenueIdStr = jwtService.getClaim(token, claims -> claims.get("foodVenueId", String.class));

            UUID participantId = participantIdStr != null ? UUID.fromString(participantIdStr) : null;
            UUID tableSessionId = tableSessionIdStr != null ? UUID.fromString(tableSessionIdStr) : null;
            UUID foodVenueId = foodVenueIdStr != null ? UUID.fromString(foodVenueIdStr) : null;

            List<org.springframework.security.core.GrantedAuthority> authorities =
                    List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));

            CustomUserPrincipal principal = new CustomUserPrincipal(
                    email,
                    null, // no necesitamos password en JWT
                    authorities,
                    RoleType.valueOf(role),
                    participantId,
                    tableSessionId,
                    foodVenueId
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            log.error("[JwtAuthenticationFilter] Token processing failed: {}", e.getMessage(), e);
            sendUnauthorizedError(response, "INVALID_OR_EXPIRED_TOKEN", "Invalid or expired token");
            return;
        }
        log.debug("[JwtAuthenticationFilter] Successfully authenticated user");
        filterChain.doFilter(request, response);
    }

    private void sendTokenExpiredError(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, "EXPIRED_TOKEN",
                "Access token has expired. Please use the refresh token to obtain a new access token.");
    }

    private void sendUnauthorizedError(HttpServletResponse response, String errorCode, String message) throws IOException {
        sendErrorResponse(response, errorCode, message);
    }

    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> error = new HashMap<>();
        error.put("error", errorCode);
        error.put("message", message);
        error.put("timestamp", Instant.now().toString());

        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        response.getWriter().flush();
    }

}

package com.group_three.food_ordering.configs.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group_three.food_ordering.utils.constants.ApiPaths;
import com.group_three.food_ordering.configs.security.CustomUserPrincipal;
import com.group_three.food_ordering.configs.security.JwtService;
import com.group_three.food_ordering.dto.SessionInfo;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
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
        log.info("[JwtAuthenticationFilter] Authorization header present: {}", authHeader != null);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[JwtAuthenticationFilter] No Bearer token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            log.debug("[JwtAuthenticationFilter] Verifying token expiration");
            if (Boolean.TRUE.equals(jwtService.isTokenExpired(token))) {
                log.error("[JwtAuthenticationFilter] Token is EXPIRED");
                sendTokenExpiredError(response);
                return;
            }

            log.debug("[JwtAuthenticationFilter] Validating access token");
            if (!Boolean.TRUE.equals(jwtService.isTokenValid(token))) {
                log.error("[JwtAuthenticationFilter] Token is INVALID");
                sendUnauthorizedError(response, "INVALID_TOKEN", "JWT token is invalid");
                return;
            }
            SessionInfo sessionInfo = jwtService.getSessionInfoFromToken(token);

            List<org.springframework.security.core.GrantedAuthority> authorities =
                    List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(sessionInfo.role()));

            CustomUserPrincipal principal = new CustomUserPrincipal(

                    sessionInfo.userId(),
                    sessionInfo.subject(),
                    authorities,
                    RoleType.valueOf(sessionInfo.role()),
                    sessionInfo.participantId(),
                    sessionInfo.tableSessionId(),
                    sessionInfo.foodVenueId()
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("[JwtAuthenticationFilter] Authentication set successfully for user: {}", sessionInfo.subject());

        } catch (Exception e) {
            log.error("[JwtAuthenticationFilter] Token processing failed: {}", e.getMessage(), e);
            sendUnauthorizedError(response, "INVALID_OR_EXPIRED_TOKEN", "Invalid or expired token");
            return;
        }

        log.info("[JwtAuthenticationFilter] Continuing filter chain");
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
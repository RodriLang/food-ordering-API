package com.group_three.food_ordering.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (Boolean.TRUE.equals(jwtService.isTokenValid(token))) {

                    String email = jwtService.getUsernameFromToken(token);
                    String role = jwtService.getClaim(token, claims -> claims.get("role", String.class));
                    String participantIdStr = jwtService.getClaim(token, claims -> claims.get("participantId", String.class));
                    String tableSessionIdStr = jwtService.getClaim(token, claims -> claims.get("tableSessionId", String.class));

                    UUID participantId = participantIdStr != null ? UUID.fromString(participantIdStr) : null;
                    UUID tableSessionId = tableSessionIdStr != null ? UUID.fromString(tableSessionIdStr) : null;

                    List<org.springframework.security.core.GrantedAuthority> authorities =
                            List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));

                    CustomUserPrincipal principal = new CustomUserPrincipal(
                            email,
                            null, // no necesitamos password en JWT
                            authorities,
                            RoleType.valueOf(role),
                            participantId,
                            tableSessionId
                    );

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    //log.debug("JWT valid for user: {}, role: {}, participantId: {}, tableSessionId: {}",
                    //        email, role, participantId, tableSessionId);
                }
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Invalid or expired token");
                error.put("error", e.getMessage());
                response.getWriter().write(new ObjectMapper().writeValueAsString(error));
                response.getWriter().flush();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

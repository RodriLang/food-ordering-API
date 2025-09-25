package com.group_three.food_ordering.context;

import com.group_three.food_ordering.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TenantContext tenantContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                log.debug("[TenantContextFilter] Extracting foodVenueId from JWT token={}", token);
                String foodVenueId = jwtService.getFoodVenueId(token);
                if (foodVenueId != null) {
                    tenantContext.setCurrentFoodVenueId(foodVenueId);
                    log.debug("[TenantContextFilter] Set currentFoodVenueId={}", foodVenueId);
                } else {
                    log.warn("[TenantContextFilter] foodVenueId extracted from token is null");
                }
            } catch (Exception e) {
                log.warn("[TenantContextFilter] Failed to extract tenant from JWT token reason={}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.group_three.food_ordering.context;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.security.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {

    private final TenantContext tenantContext;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldSkip = path.startsWith(ApiPaths.AUTH_URI + "/login")
                || path.startsWith(ApiPaths.AUTH_URI + "/register")
                || path.startsWith(ApiPaths.AUTH_URI + "/logout")
                || path.startsWith(ApiPaths.AUTH_URI + "/refresh")
                || path.startsWith(ApiPaths.ROOT_ACCESS_URI)
                || path.startsWith(ApiPaths.ROLE_SELECTOR_URI);

        if (shouldSkip) {
            log.debug("[TenantContextFilter] Request to {} will NOT be filtered", path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {

        log.debug("[TenantContextFilter] Processing request: {}", request.getRequestURI());

        try {
            log.debug("[TenantContextFilter] Getting authentication from SecurityContextHolder");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                log.debug("[TenantContextFilter] Authentication found: {}", authentication.getName());
                Object principal = authentication.getPrincipal();

                if (principal instanceof CustomUserPrincipal customPrincipal) {
                    log.debug("[TenantContextFilter] CustomUserPrincipal found");
                    UUID foodVenueId = customPrincipal.getFoodVenueId();

                    if (foodVenueId != null) {
                        tenantContext.setCurrentFoodVenueId(foodVenueId.toString());
                        log.debug("[TenantContextFilter] Set currentFoodVenueId={}", foodVenueId);
                    } else {
                        log.debug("[TenantContextFilter] foodVenueId in principal is null");
                    }
                } else {
                    log.debug("[TenantContextFilter] Principal is not CustomUserPrincipal: {}",
                            principal != null ? principal.getClass().getSimpleName() : "null");
                }
            } else {
                log.debug("[TenantContextFilter] No authenticated user in SecurityContext");
            }

        } catch (Exception e) {
            log.warn("[TenantContextFilter] Failed to extract tenant from JWT token reason={}", e.getMessage(), e);
        }
        log.debug("[TenantContextFilter] Continuing filter chain");
        filterChain.doFilter(request, response);
    }
}

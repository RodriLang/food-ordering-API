package com.group_three.food_ordering.context;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextInitializationFilter extends OncePerRequestFilter {

    private final TenantContext tenantContext;
    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith(ApiPaths.AUTH_URI + "/logout")
                || path.startsWith(ApiPaths.AUTH_URI + "/refresh")
                || path.startsWith(ApiPaths.TABLE_SESSION_URI + "/end");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req,
                                    @NotNull HttpServletResponse res,
                                    @NotNull FilterChain chain)
            throws IOException, ServletException {

        log.info("[ContextInitializationFilter] {} {}", req.getMethod(), req.getRequestURI());

        // 1) Token opcional
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                var claims = jwtService.extractAllClaims(token); // puede tirar ExpiredJwtException
                tenantContext.setClaims(claims);

                SessionInfo si = jwtService.getSessionInfoFromClaims(claims);
                tenantContext.setSessionInfo(si);

                log.debug("[ContextInitializationFilter] Session info: user={}, participant={}, tableSession={}, foodVenue={}",
                        si != null ? si.userId() : null,
                        si != null ? si.participantId() : null,
                        si != null ? si.tableSessionId() : null,
                        si != null ? si.foodVenueId() : null);
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // Si el access está vencido y no es /auth/refresh (ya excluido), dejar seguir la cadena.
                log.debug("[ContextInitializationFilter] Access token expired; skipping context init");
            }
        }

        chain.doFilter(req, res);
    }
}


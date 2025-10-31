package com.group_three.food_ordering.configs.filters;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.configs.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class ContextInitializationFilter extends OncePerRequestFilter {

    private final TenantContext tenantContext;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req,
                                    @NotNull HttpServletResponse res,
                                    @NotNull FilterChain chain)
            throws IOException, ServletException {

        log.info("[ContextInitializationFilter] {} {}", req.getMethod(), req.getRequestURI());

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                // Intenta validar y extraer claims (fallará si está vencido)
                var claims = jwtService.extractAllClaims(token);
                setContext(claims);

            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                // Si el token está vencido, extrae los claims para inicializar el contexto.
                log.warn("[ContextInitializationFilter] Access token EXPIRED, setting context from expired claims.");

                // Se obtienen los claims de la excepción
                var claims = ex.getClaims();
                setContext(claims);

            } catch (Exception ex) {
                // Captura otras excepciones de JWT (malformado, firma inválida, etc.)
                log.warn("[ContextInitializationFilter] Invalid JWT token: {}", ex.getMessage());
            }
        }

        chain.doFilter(req, res);
        log.debug("[ContextInitializationFilter] End filter");
    }

    private void setContext(Claims claims) {

        SessionInfo sessionInfo = jwtService.getSessionInfoFromClaims(claims);
        tenantContext.setSessionInfo(sessionInfo);

        log.debug("[ContextInitializationFilter] Session info from token: user={}, participant={}, tableSession={}, foodVenue={}",
                sessionInfo != null ? sessionInfo.userId() : null,
                sessionInfo != null ? sessionInfo.participantId() : null,
                sessionInfo != null ? sessionInfo.tableSessionId() : null,
                sessionInfo != null ? sessionInfo.foodVenueId() : null);

    }
}


package com.group_three.food_ordering.context;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.repositories.UserRepository;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextInitializationFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith(ApiPaths.AUTH_URI + "/logout")
                || path.startsWith(ApiPaths.AUTH_URI + "/refresh");
    }

    private final TenantContext tenantContext;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final ParticipantRepository participantRepo;
    private final TableSessionRepository tableSessionRepo;
    private final FoodVenueRepository foodVenueRepo;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws IOException, ServletException {

        log.info("[ContextInitializationFilter] Starting context initialization for request: {} {}", req.getMethod(), req.getRequestURI());
        // 1) Token opcional
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            // 2) Parsear/validar SOLO UNA VEZ
            var claims = jwtService.extractAllClaims(token);
            tenantContext.setClaims(claims);

            // 3) Construir SessionInfo según tus reglas (guest / client)
            SessionInfo sessionInfoFromClaims = jwtService.getSessionInfoFromClaims(claims);
            log.debug("[ContextInitializationFilter] SessionInfo from claims: {}", sessionInfoFromClaims);
            tenantContext.setSessionInfo(sessionInfoFromClaims);

            // 4) Cargar entidades mínimas necesarias (lazy OK)
            loadEntities(sessionInfoFromClaims);
        }

        // 5) Fallback para Tenant
        if (tenantContext.foodVenueIdOpt().isEmpty()) {
            String tenantId = req.getHeader("X-Tenant-Id");
            if (tenantId != null) {
                try {
                    UUID fid = UUID.fromString(tenantId);
                    foodVenueRepo.findByPublicId(fid).ifPresent(tenantContext::setFoodVenue);
                } catch (IllegalArgumentException ignored) {
                    log.warn("Invalid tenant id {}", tenantId);
                }
            }
        }

        chain.doFilter(req, res);
    }

    private void loadEntities(SessionInfo sessionInfoFromClaims) {
        if (sessionInfoFromClaims != null) {
            if (sessionInfoFromClaims.userId() != null)
                userRepo.findByPublicId(sessionInfoFromClaims.userId()).ifPresent(tenantContext::setUser);
            if (sessionInfoFromClaims.participantId() != null)
                participantRepo.findByPublicId(sessionInfoFromClaims.participantId()).ifPresent(tenantContext::setParticipant);
            if (sessionInfoFromClaims.tableSessionId() != null)
                tableSessionRepo.findByPublicId(sessionInfoFromClaims.tableSessionId()).ifPresent(tenantContext::setTableSession);
            if (sessionInfoFromClaims.foodVenueId() != null)
                foodVenueRepo.findByPublicId(sessionInfoFromClaims.foodVenueId()).ifPresent(tenantContext::setFoodVenue);
        }
        log.debug("[ContextInitializationFilter] Loaded entities: user={}, participant={}, tableSession={}, foodVenue={}",
                tenantContext.userOpt().map(u -> u.getPublicId().toString()).orElse("null"),
                tenantContext.participantOpt().map(p -> p.getPublicId().toString()).orElse("null"),
                tenantContext.tableSessionOpt().map(ts -> ts.getPublicId().toString()).orElse("null"),
                tenantContext.foodVenueOpt().map(fv -> fv.getPublicId().toString()).orElse("null"));
    }
}

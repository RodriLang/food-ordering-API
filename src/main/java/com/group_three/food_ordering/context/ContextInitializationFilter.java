package com.group_three.food_ordering.context;

import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.security.JwtService;
import io.jsonwebtoken.Claims;
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

    private final RequestContext requestContext;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final ParticipantRepository participantRepo;
    private final TableSessionRepository tableSessionRepo;
    private final FoodVenueRepository foodVenueRepo;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
            throws IOException, ServletException {

        // 1) Token opcional
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            requestContext.setBearerToken(token);

            // 2) Parse/validate SOLO UNA VEZ
            var claims = jwtService.extractAllClaims(token);
            requestContext.setClaims(claims);

            // 3) Construir SessionInfo según tus reglas (guest / client)
            SessionInfo si = buildSessionInfo(claims);
            requestContext.setSessionInfo(si);

            // 4) Cargar entidades mínimas necesarias (lazy OK)
            if (si != null) {
                if (si.userId() != null) userRepo.findByPublicId(si.userId()).ifPresent(requestContext::setUser);
                if (si.participantId() != null) participantRepo.findByPublicId(si.participantId()).ifPresent(requestContext::setParticipant);
                if (si.tableSessionId() != null) tableSessionRepo.findByPublicId(si.tableSessionId()).ifPresent(requestContext::setTableSession);
                if (si.foodVenueId() != null) foodVenueRepo.findByPublicId(si.foodVenueId()).ifPresent(requestContext::setFoodVenue);
            }
        }

        // 5) Fallback para Tenant (si usás header X-Tenant-Id)
        if (requestContext.foodVenueIdOpt().isEmpty()) {
            String tenantId = req.getHeader("X-Tenant-Id");
            if (tenantId != null) {
                try {
                    UUID fid = UUID.fromString(tenantId);
                    foodVenueRepo.findByPublicId(fid).ifPresent(requestContext::setFoodVenue);
                } catch (IllegalArgumentException ignored) {
                    log.warn("Invalid tenant id {}", tenantId);
                }
            }
        }

        chain.doFilter(req, res);
    }

    private SessionInfo buildSessionInfo(Claims c) {
        if (c == null) return null;
        // mapear claims → SessionInfo (tu JwtService ya lo hace: reutilizalo)
        return jwtService.getSessionInfoFromClaims(c);
    }
}

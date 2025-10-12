package com.group_three.food_ordering.context;

import com.group_three.food_ordering.dto.AuditorUser;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.utils.EntityName;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class TenantContext {

    private Claims claims;
    private User user;
    private Participant participant;
    private TableSession tableSession;
    private FoodVenue foodVenue;
    private SessionInfo sessionInfo;

    public Optional<User> userOpt()                 { return Optional.ofNullable(user); }
    public Optional<Participant> participantOpt()   { return Optional.ofNullable(participant); }
    public Optional<TableSession> tableSessionOpt() { return Optional.ofNullable(tableSession); }
    public Optional<FoodVenue> foodVenueOpt()       { return Optional.ofNullable(foodVenue); }
    public Optional<UUID> foodVenueIdOpt()          { return foodVenueOpt().map(FoodVenue::getPublicId); }


    public User requireUser() {
        return userOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.AUTH_USER));
    }
    public FoodVenue requireFoodVenue() {
        return foodVenueOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.FOOD_VENUE));
    }
    public TableSession requireTableSession() {
        return tableSessionOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.TABLE_SESSION));
    }
    public Participant requireParticipant() {
        return participantOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.PARTICIPANT));
    }

    public RoleType getRole() { return roleOpt().orElse(null); }

    public Optional<RoleType> roleOpt() {
        // 1) priorizar rol del SessionInfo (ya validaste al emitir el token)
        if (sessionInfo != null && sessionInfo.role() != null && !sessionInfo.role().isBlank()) {
            try { return Optional.of(RoleType.valueOf(sessionInfo.role())); } catch (IllegalArgumentException ignored) {
                log.warn("SessionInfo with unknown role={}", sessionInfo.role());
                return Optional.empty();
            }
        }
        // 2) fallback a claims si existen
        if (claims == null) return Optional.empty();
        Object raw = claims.get("role");
        if (raw == null) return Optional.empty();
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) return Optional.empty();
        s = s.toUpperCase(Locale.ROOT);
        if (!s.startsWith("ROLE_")) s = "ROLE_" + s;
        try { return Optional.of(RoleType.valueOf(s)); }
        catch (IllegalArgumentException ex) {
            log.warn("Token con role desconocido: {}", s);
            return Optional.empty();
        }
    }


    public boolean isAuthenticated() { return user != null; }
    public boolean isGuest() { return sessionInfo != null && "ROLE_GUEST".equals(sessionInfo.role()); }

    void setClaims(Claims c) { this.claims = c; }
    void setSessionInfo(SessionInfo s) { this.sessionInfo = s; }
    void setUser(User u) { this.user = u; }
    void setParticipant(Participant p) { this.participant = p; }
    void setTableSession(TableSession ts) { this.tableSession = ts; }
    void setFoodVenue(FoodVenue fv) { this.foodVenue = fv; }

    public SessionInfo session() { return sessionInfo; }

    public AuditorUser requireAuditorUser() {
        // Si hay User, priorizarlo (auditor ideal)
        if (user != null) {
            return new AuditorUser(user.getPublicId(), user.getEmail());
        }
        // Fallback: si es invitado pero el token tiene subject, lo usamos (id null)
        if (sessionInfo != null && sessionInfo.subject() != null) {
            return new AuditorUser(null, sessionInfo.subject());
        }
        // Sin identidad en el request
        throw new EntityNotFoundException(EntityName.AUDITOR_USER);
    }

}
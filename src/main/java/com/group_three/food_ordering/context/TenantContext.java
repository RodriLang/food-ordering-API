package com.group_three.food_ordering.context;

import com.group_three.food_ordering.dto.AuditorUser;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.utils.EntityName;
import io.jsonwebtoken.Claims;
import lombok.Getter;
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

    // --- Repos para lazy-loading ---
    private final UserRepository userRepo;
    private final ParticipantRepository participantRepo;
    private final TableSessionRepository tableSessionRepo;
    private final FoodVenueRepository foodVenueRepo;

    // --- Estado actual del request ---
    private Claims claims;
    private SessionInfo sessionInfo = SessionInfo.builder().role(RoleType.ROLE_ADMIN.name()).build();
    private User user;
    private Participant participant;
    private TableSession tableSession;
    private FoodVenue foodVenue;

    // --- IDs para lazy-loading ---
    @Getter
    private UUID userId;
    @Getter
    private UUID participantId;
    @Getter
    private UUID tableSessionId;
    @Getter
    private UUID foodVenueId = UUID.fromString("6ae17fe9-a6af-11f0-9d49-fc5cee3effed");

    // --- Flags de memoization (cachear incluso null) ---
    private boolean userResolved;
    private boolean participantResolved;
    private boolean tableSessionResolved;
    private boolean foodVenueResolved;

    // =========================
    // Setters de contexto/IDs
    // =========================

    void setClaims(Claims c) {
        this.claims = c;
    }

    void setSessionInfo(SessionInfo s) {
        this.sessionInfo = s;
        // poblar IDs si vienen en el token
        if (s != null) {

            this.foodVenueId = s.foodVenueId();
            this.tableSessionId = s.tableSessionId();
            this.userId = s.userId();
            this.participantId = s.participantId();
        }
    }

    void setFoodVenueIdHeader(UUID foodVenueId) {
        this.foodVenueId = foodVenueId;
    }

    void setUser(User u) {
        this.user = u;
        this.userResolved = true;
        this.userId = (u != null ? u.getPublicId() : null);
    }

    void setParticipant(Participant p) {
        this.participant = p;
        this.participantResolved = true;
        this.participantId = (p != null ? p.getPublicId() : null);
    }

    void setTableSession(TableSession ts) {
        this.tableSession = ts;
        this.tableSessionResolved = true;
        this.tableSessionId = (ts != null ? ts.getPublicId() : null);
    }

    void setFoodVenue(FoodVenue fv) {
        this.foodVenue = fv;
        this.foodVenueResolved = true;
        this.foodVenueId = (fv != null ? fv.getPublicId() : null);
    }

    // =========================
    // Getters lazy con memoization
    // =========================

    public Optional<User> userOpt() {
        if (!userResolved) {
            if(participantOpt().isPresent()) {
                user = requireParticipant().getUser();
                userResolved = true;
            } else if (userId != null) {
                user = userRepo.findByPublicIdAndDeletedFalse(userId).orElse(null);
                userResolved = true;
            }
            log.debug("[TenantContext] user loaded? {}", user != null);
        }
        log.debug("[TenantContext] Getting user cache");
        return Optional.ofNullable(user);
    }

    public Optional<Participant> participantOpt() {
        if (!participantResolved) {
            participant = (participantId != null) ? participantRepo.findByPublicId(participantId).orElse(null) : null;
            participantResolved = true;
            log.debug("[TenantContext] participant loaded? {}", participant != null);
        }
        log.debug("[TenantContext] Getting participant cache");
        return Optional.ofNullable(participant);
    }

    public Optional<TableSession> tableSessionOpt() {
        if (!tableSessionResolved) {
            if(participantOpt().isPresent()) {
                tableSession = requireParticipant().getTableSession();
                tableSessionResolved = true;
            }
            log.debug("[TenantContext] tableSession loaded? {}", tableSession != null);
        }
        log.debug("[TenantContext] Getting table session cache");
        return Optional.ofNullable(tableSession);
    }

    public Optional<FoodVenue> foodVenueOpt() {
        if (!foodVenueResolved) {
            if(tableSessionOpt().isPresent()) {
                foodVenue = requireTableSession().getFoodVenue();
            } else {
                foodVenue = (foodVenueId != null) ? foodVenueRepo.findByPublicIdAndDeletedFalse(foodVenueId).orElse(null) : null;
            }
            foodVenueResolved = true;
            log.debug("[TenantContext] foodVenue loaded? {}", foodVenue != null);
        }
        log.debug("[TenantContext] Getting food venue cache");
        return Optional.ofNullable(foodVenue);
    }

    public Optional<UUID> foodVenueIdOpt() {
        UUID id;
        if ((foodVenueId != null)) {
            id = foodVenueId;
        } else {
            if (foodVenue != null) id = foodVenue.getPublicId();
            else id = null;
        }
        return Optional.ofNullable(id);
    }

    // =========================
    // Require helpers (igual que antes)
    // =========================

    public User requireUser() {
        log.debug("[TenantContext] requireUser called");
        return userOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.AUTH_USER));
    }

    public FoodVenue requireFoodVenue() {
        log.debug("[TenantContext] requireFoodVenue called");
        return foodVenueOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.FOOD_VENUE));
    }

    public TableSession requireTableSession() {
        log.debug("[TenantContext] requireTableSession called");
        return tableSessionOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.TABLE_SESSION));
    }

    public Participant requireParticipant() {
        log.debug("[TenantContext] requireParticipant called");
        return participantOpt().orElseThrow(() -> new EntityNotFoundException(EntityName.PARTICIPANT));
    }

    // =========================
    // Rol y estado auth
    // =========================

    public RoleType getRole() {
        return roleOpt().orElse(null);
    }

    public Optional<RoleType> roleOpt() {
        // 1) priorizar rol del SessionInfo (ya validado al emitir el token)
        if (sessionInfo != null && sessionInfo.role() != null && !sessionInfo.role().isBlank()) {
            try {
                return Optional.of(RoleType.valueOf(sessionInfo.role()));
            } catch (IllegalArgumentException ignored) {
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
        try {
            return Optional.of(RoleType.valueOf(s));
        } catch (IllegalArgumentException ex) {
            log.warn("Token con role desconocido: {}", s);
            return Optional.empty();
        }
    }

    public boolean isAuthenticated() {
        return userOpt().isPresent();
    }

    public boolean isGuest() {
        return roleOpt().map(r -> r == RoleType.ROLE_GUEST).orElse(false);
    }

    public SessionInfo session() {
        return sessionInfo;
    }

    public AuditorUser requireAuditorUser() {
        if (userOpt().isPresent()) {
            User u = user; // ya cargado por userOpt()
            return new AuditorUser(u.getPublicId(), u.getEmail());
        }
        if (sessionInfo != null && sessionInfo.subject() != null) {
            return new AuditorUser(null, sessionInfo.subject());
        }
        throw new EntityNotFoundException(EntityName.AUDITOR_USER);
    }

    // =========================
    // Invalidaciones de cache
    // =========================

    public void invalidateUser() {
        this.user = null;
        this.userResolved = false;
        log.debug("[TenantContext] User invalidated");
    }

    public void invalidateParticipant() {
        this.participant = null;
        this.participantResolved = false;
        log.debug("[TenantContext] Participant invalidated");
    }

    public void invalidateTableSession() {
        this.tableSession = null;
        this.tableSessionResolved = false;
        log.debug("[TenantContext] TableSession invalidated");
    }

    public void invalidateFoodVenue() {
        this.foodVenue = null;
        this.foodVenueResolved = false;
        log.debug("[TenantContext] FoodVenue invalidated");
    }
}

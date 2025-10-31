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
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.utils.EntityName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

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
    private final FoodVenueRepository foodVenueRepo;

    // --- Estado actual del request ---
    private SessionInfo sessionInfo;
    private User user;
    private Participant participant;
    private TableSession tableSession;
    private FoodVenue foodVenue;
    private AuditorUser cachedAuditorUser;

    // --- IDs para lazy-loading ---
    @Getter
    private UUID userId;
    @Getter
    private UUID participantId;
    @Getter
    private UUID tableSessionId;
    @Getter
    private UUID foodVenueId;

    // --- Flags de memoization (cachear incluso null) ---
    private boolean userResolved = false;
    private boolean participantResolved = false;
    private boolean tableSessionResolved = false;
    private boolean foodVenueResolved = false;
    private boolean auditorUserResolved = false;

    // =========================
    // Setters de contexto/IDs
    // =========================

    public void setSessionInfo(SessionInfo s) {
        this.sessionInfo = s;
        // poblar IDs si vienen en el token
        if (s != null) {

            this.foodVenueId = s.foodVenueId();
            this.tableSessionId = s.tableSessionId();
            this.userId = s.userId();
            this.participantId = s.participantId();
        }
    }

    // =========================
    // Getters lazy con memoization
    // =========================

    public Optional<User> userOpt() {
        log.debug("[TenantContext] Getting user cache");
        if (!userResolved) {
            if (participantOpt().isPresent()) {
                log.debug("[TenantContext] Getting user from participant");
                user = requireParticipant().getUser();
                userResolved = true;
            } else if (userId != null) {
                log.debug("[TenantContext] Getting user from user: {}", userId);
                user = userRepo.findByPublicIdAndDeletedFalse(userId).orElse(null);
                userResolved = true;
            }
            log.debug("[TenantContext] user loaded? {}", user != null);
        }
        return Optional.ofNullable(user);
    }

    public Optional<Participant> participantOpt() {
        log.debug("[TenantContext] Getting participant cache");
        if (!participantResolved) {
            log.debug("[TenantContext] Getting participant from participantId: {}", participantId);
            participant = (participantId != null) ? participantRepo.findByPublicId(participantId).orElse(null) : null;
            participantResolved = true;
            log.debug("[TenantContext] participant loaded? {}", participant != null);
        }
        return Optional.ofNullable(participant);
    }

    public Optional<TableSession> tableSessionOpt() {
        if (!tableSessionResolved) {
            if (participantOpt().isPresent()) {
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
            if (tableSessionOpt().isPresent()) {
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
    // Require helpers
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
        return Optional.empty();
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
        log.debug("[TenantContext] Require Auditor User called");

        if (auditorUserResolved) {
            if (cachedAuditorUser == null) {
                // Ahora solo se lanzará si la resolución falló previamente
                throw new EntityNotFoundException(EntityName.AUDITOR_USER);
            }
            return cachedAuditorUser;
        }

        log.debug("[TenantContext] Auditor User not cached, resolving...");

        if (sessionInfo != null && sessionInfo.userId() != null && sessionInfo.subject() != null) {
            log.debug("[TenantContext] Getting auditor user from SessionInfo (User ID + Subject)");
            cachedAuditorUser = new AuditorUser(sessionInfo.userId(), sessionInfo.subject());
        } else if (userOpt().isPresent()) {
            log.debug("[TenantContext] Getting auditor user from resolved User entity");
            User u = user;
            cachedAuditorUser = new AuditorUser(u.getPublicId(), u.getEmail());
        } else if (sessionInfo != null && sessionInfo.subject() != null) {
            log.debug("[TenantContext] Getting auditor user from SessionInfo subject only (ID is null)");
            cachedAuditorUser = new AuditorUser(null, sessionInfo.subject());
        } else {
            log.debug("[TenantContext] No auditor user found");
            auditorUserResolved = true;
            throw new EntityNotFoundException(EntityName.AUDITOR_USER);
        }
        log.debug("[TenantContext] Auditor user resolved: {}", cachedAuditorUser);
        auditorUserResolved = true;
        return cachedAuditorUser;
    }
}

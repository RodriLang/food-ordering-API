package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.RequestContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidPaymentStatusException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.DiningTableService;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.TableSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.group_three.food_ordering.utils.EntityName.PARTICIPANT;
import static com.group_three.food_ordering.utils.EntityName.TABLE_SESSION;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TableSessionServiceImpl implements TableSessionService {

    private final TableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final ParticipantService participantService;
    private final RequestContext requestContext;
    private final JwtService jwtService;
    private final DiningTableService diningTableService;
    private final ParticipantMapper participantMapper;

    // ===========================
    // Entrar / Asociarse a mesa
    // ===========================
    @Override
    public AuthResponse enter(TableSessionRequestDto dto) {
        log.debug("[TableSession] Processing table session entry for tableId={}", dto.getTableId());

        // 1) Mesa y venue (entrada siempre confiable, sin mutar "contextos" globales)
        DiningTable diningTable = diningTableService.getEntityById(dto.getTableId());
        FoodVenue venue = diningTable.getFoodVenue();

        // 2) Snapshot del estado actual del request
        Optional<User> userOpt         = requestContext.userOpt();
        Optional<Participant> partOpt  = requestContext.participantOpt();
        Optional<RoleType> roleOpt     = requestContext.roleOpt();
        Optional<TableSession> tsOpt   = requestContext.tableSessionOpt();

        // 3) Idempotencia: si es CLIENTE con sesión activa en su token → devolver esa sesión
        if (roleOpt.map(r -> r == RoleType.ROLE_CLIENT).orElse(false)
                && tsOpt.isPresent()
                && tsOpt.get().getEndTime() == null) {
            log.debug("[TableSession] Idempotent enter: existing active session {}", tsOpt.get().getPublicId());
            return signForParticipant(tsOpt.get(), Objects.requireNonNull(findOrCreateParticipantForUser(tsOpt.get(), userOpt.orElse(null))));
        }

        // 4) Buscar si la mesa ya tiene sesión activa
        TableSession activeForTable = tableSessionRepository
                .findTableSessionByDiningTable_PublicIdAndDiningTableStatusAndEndTimeIsNull(
                        diningTable.getPublicId(), DiningTableStatus.IN_SESSION)
                .orElse(null);

        // 5) CLIENTE: priorizar una sesión activa propia; si no, unirse/crear en esta mesa
        if (roleOpt.map(r -> r == RoleType.ROLE_CLIENT).orElse(false) && userOpt.isPresent()) {
            User user = userOpt.get();

            // 5a) ¿Usuario ya tiene sesión activa (aunque su token no la traiga)?
            TableSession userActive = findActiveSessionForUser(user);
            if (userActive != null) {
                log.debug("[TableSession] User {} already has an active session {}", user.getEmail(), userActive.getPublicId());
                return signForParticipant(userActive, findOrCreateParticipantForUser(userActive, user));
            }

            // 5b) Vincular al cliente a la sesión de la mesa (reusar o crear)
            TableSession ts = (activeForTable != null) ? activeForTable : createNewSession(diningTable, venue);
            Participant client = findOrCreateParticipantForUser(ts, user);

            // Si venía con participant guest de ESTA sesión, migrar órdenes y remover guest
            partOpt.filter(p -> p.getRole() == RoleType.ROLE_GUEST)
                    .filter(g -> g.getTableSession() != null && ts.getId().equals(g.getTableSession().getId()))
                    .ifPresent(guest -> migrateGuestToClient(ts, guest, client));

            return signForParticipant(ts, client);
        }

        // 6) Invitado o anónimo → reusar/crear sesión y participant guest
        TableSession ts = (activeForTable != null) ? activeForTable : createNewSession(diningTable, venue);
        Participant guest = findOrCreateGuestParticipant(ts, partOpt.orElse(null));
        return signForParticipant(ts, guest);
    }

    // ===========================
    // Queries
    // ===========================
    @Override
    public Page<TableSessionResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findByFoodVenuePublicId(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public TableSessionResponseDto getById(UUID id) {
        return tableSessionMapper.toDto(getEntityById(id));
    }

    @Override
    public TableSessionResponseDto getByCurrentParticipant() {
        Participant current = requestContext.requireParticipant();
        TableSession ts = tableSessionRepository.findByParticipantsContains(List.of(current))
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPANT));
        return tableSessionMapper.toDto(ts);
    }

    @Override
    public TableSession getEntityById(UUID sessionId) {
        return tableSessionRepository.findByPublicId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, sessionId.toString()));
    }

    @Override
    public Page<TableSessionResponseDto> getByFoodVenueAndTable(UUID foodVenueId, Integer tableNumber, Pageable pageable) {
        return tableSessionRepository.findByFoodVenuePublicIdAndDiningTableNumber(foodVenueId, tableNumber, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByContextAndTable(Integer tableNumber, Pageable pageable) {
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findByFoodVenuePublicIdAndDiningTableNumber(foodVenueId, tableNumber, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByTableAndTimeRange(Integer tableNumber, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        LocalDateTime effectiveEnd = (end == null) ? LocalDateTime.now() : end;
        if (start.isAfter(effectiveEnd)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository
                .findByFoodVenuePublicIdAndDiningTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
                        foodVenueId, tableNumber, start, effectiveEnd, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getActiveSessions(Pageable pageable) {
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findByFoodVenuePublicIdAndEndTimeIsNull(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByHostClient(UUID clientId, Pageable pageable) {
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(foodVenueId, clientId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByAuthUserHostClient(Pageable pageable) {
        // FIX: antes se usaba tableSessionId como "host"; debería ser el Participant host actual
        UUID hostParticipantId = requestContext.requireParticipant().getPublicId();
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(foodVenueId, hostParticipantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByParticipant(UUID participantId, Pageable pageable) {
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findPastSessionsByParticipantIdAndDeletedFalse(foodVenueId, participantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByAuthUserParticipant(Pageable pageable) {
        // FIX: usar el participant actual, no el user
        UUID participantId = requestContext.requireParticipant().getPublicId();
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        return tableSessionRepository.findPastSessionsByParticipantIdAndDeletedFalse(foodVenueId, participantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public TableSessionResponseDto getLatestByTable(UUID tableId) {
        UUID foodVenueId = requestContext.requireFoodVenue().getPublicId();
        TableSession ts = tableSessionRepository
                .findTopByFoodVenuePublicIdAndDiningTablePublicIdOrderByStartTimeDesc(foodVenueId, tableId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
        return tableSessionMapper.toDto(ts);
    }

    @Override
    public TableSessionResponseDto addClient(UUID sessionId, UUID clientId) {
        TableSession ts = getEntityById(sessionId);
        Participant p = participantService.getEntityById(clientId);
        ts.getParticipants().add(p);
        return tableSessionMapper.toDto(tableSessionRepository.save(ts));
    }

    // ===========================
    // Cierre de sesión
    // ===========================
    @Override
    public TableSessionResponseDto closeCurrentSession() {
        Participant currentHost = requestContext.requireParticipant();
        TableSession currentTs = requestContext.requireTableSession();
        if (!currentTs.getSessionHost().getPublicId().equals(currentHost.getPublicId())) {
            throw new AccessDeniedException("Only the current host can end the session");
        }
        return closeSession(currentTs);
    }

    @Override
    public TableSessionResponseDto closeSessionById(UUID tableId) {
        TableSession ts = findActiveSessionByDiningTable(tableId);
        return closeSession(ts);
    }

    private TableSessionResponseDto closeSession(TableSession ts) {
        List<Order> orders = ts.getOrders();
        boolean completedPayments = orders.stream()
                .map(o -> o.getPayment().getStatus())
                .allMatch(PaymentStatus.COMPLETED::equals);
        if (!completedPayments) {
            throw new InvalidPaymentStatusException("All payments must be paid to finish table session");
        }
        ts.setEndTime(LocalDateTime.now());
        diningTableService.updateStatus(DiningTableStatus.WAITING_RESET, ts.getDiningTable().getPublicId());
        tableSessionRepository.save(ts);
        return tableSessionMapper.toDto(ts);
    }

    // ===========================
    // Helpers internos
    // ===========================
    private TableSession createNewSession(DiningTable table, FoodVenue venue) {
        log.debug("[TableSession] Creating new session for table={}", table.getPublicId());
        TableSession ts = TableSession.builder()
                .diningTable(table)
                .foodVenue(venue)
                .startTime(LocalDateTime.now())
                .diningTable(table)
                .build();
        ts = tableSessionRepository.save(ts);

        // Primer participante define host si corresponde (se setea al unir participante)
        return ts;
    }

    private Participant findOrCreateParticipantForUser(TableSession ts, User user) {
        if (user == null) return null;
        Optional<Participant> existing = ts.getParticipants().stream()
                .filter(p -> p.getUser() != null && Objects.equals(p.getUser().getId(), user.getId()))
                .findFirst();

        if (existing.isPresent()) return existing.get();

        Participant p = participantService.create(user, ts); // respeta tus reglas (ROLE_CLIENT, nickname, etc.)
        // Si no hay host aún, el primero en entrar es host
        if (ts.getSessionHost() == null) {
            ts.setSessionHost(p);
        }
        ts.getParticipants().add(p);
        tableSessionRepository.save(ts);
        return p;
    }

    private Participant findOrCreateGuestParticipant(TableSession ts, Participant currentGuestFromCtx) {
        // Reusar si el token ya trae participant guest de esta sesión
        if (currentGuestFromCtx != null
                && currentGuestFromCtx.getRole() == RoleType.ROLE_GUEST
                && currentGuestFromCtx.getTableSession() != null
                && Objects.equals(currentGuestFromCtx.getTableSession().getId(), ts.getId())) {
            return currentGuestFromCtx;
        }
        Participant p = participantService.create(null, ts); // crea invitado (ROLE_GUEST)
        if (ts.getSessionHost() == null) {
            ts.setSessionHost(p); // si es el primero, queda como host
        }
        ts.getParticipants().add(p);
        tableSessionRepository.save(ts);
        return p;
    }

    private void migrateGuestToClient(TableSession ts, Participant guest, Participant client) {
        log.debug("[TableSession] Migrating guest {} to client {}", guest.getPublicId(), client.getPublicId());
        // Reasignar órdenes del guest al client
        ts.getOrders().stream()
                .filter(o -> o.getParticipant() != null && Objects.equals(o.getParticipant().getId(), guest.getId()))
                .forEach(o -> o.setParticipant(client));

        // Remover guest de la sesión
        ts.getParticipants().remove(guest);
        participantService.softDelete(guest.getPublicId());
        tableSessionRepository.save(ts);
    }

    private TableSession findActiveSessionByDiningTable(UUID tableId) {
        return tableSessionRepository
                .findTableSessionByDiningTable_PublicIdAndDiningTableStatusAndEndTimeIsNull(tableId, DiningTableStatus.IN_SESSION)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
    }

    /**
     * Devuelve la sesión activa del usuario (si existe), usando primero el contexto y luego la DB.
     */
    private TableSession findActiveSessionForUser(User user) {
        // 1) Si el token ya trae sesión activa
        if (requestContext.tableSessionOpt().filter(ts -> ts.getEndTime() == null).isPresent()) {
            return requestContext.tableSessionOpt().orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
        }
        // 2) Query por email (tu repo ya la tiene)
        return tableSessionRepository.findActiveSessionByUserEmailAndDeletedFalse(user.getEmail()).orElse(null);
    }

    private AuthResponse signForParticipant(TableSession ts, Participant participant) {
        // Armamos SessionInfo completa para el token
        List<ParticipantResponseDto> participantsDto = ts.getParticipants().stream()
                .map(participantMapper::toResponseDto)
                .collect(Collectors.toList());

        SessionInfo si = SessionInfo.builder()
                .userId(participant.getUser() != null ? participant.getUser().getPublicId() : null)
                .subject(participant.getUser() != null ? participant.getUser().getEmail() : participant.getNickname())
                .foodVenueId(ts.getFoodVenue().getPublicId())
                .participantId(participant.getPublicId())
                .tableSessionId(ts.getPublicId())
                .role(RoleType.ROLE_CLIENT.name())
                .startTime(ts.getStartTime())
                .endTime(ts.getEndTime())
                .hostClient(participantMapper.toResponseDto(ts.getSessionHost()))
                .participants(participantsDto)
                .tableNumber(ts.getDiningTable().getNumber())
                .build();

        String access = jwtService.generateAccessToken(si);
        Instant exp = jwtService.getExpirationDateFromToken(access);

        return AuthResponse.builder()
                .accessToken(access)
                .expirationDate(exp)
                .tableNumber(si.tableNumber())
                .startTime(si.startTime())
                .endTime(si.endTime())
                .participants(si.participants())
                .hostClient(si.hostClient())
                .numberOfParticipants(si.participants() != null ? si.participants().size() : null)
                .build();
    }
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
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
    private final TenantContext tenantContext;
    private final JwtService jwtService;
    private final DiningTableService diningTableService;
    private final ParticipantMapper participantMapper;

    // ===========================
    // Entrar / Asociarse a mesa
    // ===========================
    @Override
    public AuthResponse enter(TableSessionRequestDto dto) {
        log.debug("[TableSession] Processing table session entry for tableId={}", dto.getTableId());

        DiningTable diningTable = diningTableService.getEntityById(dto.getTableId());
        log.debug("[TableSession] DiningTable number={}", diningTable.getNumber());
        FoodVenue venue = diningTable.getFoodVenue();

        // Snapshot del estado actual del request
        Optional<User> userOpt = tenantContext.userOpt();
        Optional<Participant> partOpt = tenantContext.participantOpt();
        Optional<RoleType> roleOpt = tenantContext.roleOpt();
        Optional<TableSession> tsOpt = tenantContext.tableSessionOpt();

        // CLIENTE con sesión activa en su token → devolver esa sesión
        if (roleOpt.map(r -> r == RoleType.ROLE_CLIENT).orElse(false)
                && tsOpt.isPresent()
                && tsOpt.get().getEndTime() == null) {
            log.debug("[TableSession] Idempotent enter: existing active session {}", tsOpt.get().getPublicId());
            return signForParticipant(tsOpt.get(),
                    Objects.requireNonNull(findOrCreateParticipantForUser(tsOpt.get(), userOpt.orElse(null))));
        }

        // Buscar si la mesa ya tiene sesión activa
        Optional<TableSession> activeForTableOpt = getTableSessionActiveByTable(diningTable.getPublicId());

        // CLIENTE: priorizar su sesión activa; si no, unirse/crear en esta mesa
        if (roleOpt.map(r -> r == RoleType.ROLE_CLIENT).orElse(false) && userOpt.isPresent()) {
            User user = userOpt.get();

            TableSession userActive = findActiveSessionForUser(user);
            if (userActive != null) {
                return signForParticipant(userActive, findOrCreateParticipantForUser(userActive, user));
            }

            // Vincular al cliente a la sesión de la mesa (reusar o crear)
            TableSession ts = activeForTableOpt.orElseGet(() -> createNewSession(diningTable, venue));
            Participant client = findOrCreateParticipantForUser(ts, user);

            // Si venía con participant guest de ESTA sesión, migrar órdenes y remover guest
            partOpt.filter(p -> p.getRole() == RoleType.ROLE_GUEST)
                    .filter(g -> g.getTableSession() != null && ts.getId().equals(g.getTableSession().getId()))
                    .ifPresent(guest -> migrateGuestToClient(ts, guest, client));

            return signForParticipant(ts, client);
        }

        // 6) Invitado o anónimo → reusar/crear sesión y participant guest
        TableSession ts = activeForTableOpt.orElseGet(() -> createNewSession(diningTable, venue));
        Participant guest = findOrCreateGuestParticipant(ts, partOpt.orElse(null));
        return signForParticipant(ts, guest);
    }

    // ===========================
    // Queries
    // ===========================
    @Override
    public Page<TableSessionResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[TableSessionRepository] Calling findByFoodVenuePublicId for venueId={}", foodVenueId);
        return tableSessionRepository.findByFoodVenuePublicId(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public TableSessionResponseDto getById(UUID id) {
        return tableSessionMapper.toDto(getEntityById(id));
    }

    @Override
    public TableSessionResponseDto getByCurrentParticipant() {
        Participant current = tenantContext.requireParticipant();
        log.debug("[TableSessionRepository] Calling findByParticipants for participantId={}", current.getPublicId());
        TableSession ts = tableSessionRepository.findByParticipantsContains(List.of(current))
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPANT));
        return tableSessionMapper.toDto(ts);
    }

    @Override
    public TableSession getEntityById(UUID sessionId) {
        log.debug("[TableSessionRepository] Calling findByPublicId for sessionId={}", sessionId);
        return tableSessionRepository.findByPublicId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, sessionId.toString()));
    }

    @Override
    public Page<TableSessionResponseDto> getByFoodVenueAndTable(UUID foodVenueId, Integer tableNumber, Pageable pageable) {
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndTableNumber for venueId={}, tableNumber={}",
                foodVenueId, tableNumber);
        return tableSessionRepository.findByFoodVenuePublicIdAndDiningTableNumber(foodVenueId, tableNumber, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByContextAndTable(Integer tableNumber, Pageable pageable) {
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        return getByFoodVenueAndTable(foodVenueId, tableNumber, pageable);
    }

    @Override
    public Page<TableSessionResponseDto> getByTableAndTimeRange(
            Integer tableNumber,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        LocalDateTime effectiveEnd = (end == null) ? LocalDateTime.now() : end;
        if (start.isAfter(effectiveEnd)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndTableAndTimeRange for venueId={}, " +
                "tableNumber={}, range: {} to {}", foodVenueId, tableNumber, start, effectiveEnd);
        return tableSessionRepository
                .findByFoodVenuePublicIdAndDiningTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
                        foodVenueId, tableNumber, start, effectiveEnd, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getActiveSessions(Pageable pageable) {
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndEndTimeIsNull for venueId={}", foodVenueId);
        return tableSessionRepository.findByFoodVenuePublicIdAndEndTimeIsNull(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByHostClient(UUID clientId, Pageable pageable) {
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndSessionHost for venueId={}, clientId={}",
                foodVenueId, clientId);
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(foodVenueId, clientId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByAuthUserHostClient(Pageable pageable) {
        // FIX: antes se usaba tableSessionId como "host"; debería ser el Participant host actual
        UUID hostParticipantId = tenantContext.requireParticipant().getPublicId();
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[TableSessionRepository] Calling findByFoodVenuePublicIdAndSessionHostPublicId for venueId={}, hostParticipantId={}", foodVenueId, hostParticipantId);
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(foodVenueId, hostParticipantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByParticipant(UUID participantId, Pageable pageable) {
        UUID foodVenueId = tenantContext.requireFoodVenue().getPublicId();
        log.debug("[TableSessionRepository] Calling findPastSessionsByParticipant for venueId={}, participantId={}",
                foodVenueId, participantId);

        return tableSessionRepository.findPastSessionsByParticipantIdAndDeletedFalse(foodVenueId, participantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByAuthUserParticipant(Pageable pageable) {
        UUID participantId = tenantContext.requireParticipant().getPublicId();
        return getPastByParticipant(participantId, pageable);
    }

    @Override
    public TableSessionResponseDto getLatestByTable(UUID tableId) {
        TableSession tableSession = getTableSessionActiveByTable(tableId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
        return tableSessionMapper.toDto(tableSession);
    }

    @Override
    public TableSessionResponseDto addClient(UUID sessionId, UUID clientId) {
        TableSession tableSession = getEntityById(sessionId);
        Participant participant = participantService.getEntityById(clientId);
        tableSession.getParticipants().add(participant);
        determineTableStatusPostSessionCreation(tableSession.getDiningTable(), tableSession);
        log.debug("[TableSessionRepository] Calling save to add client to session {}", sessionId);
        return tableSessionMapper.toDto(tableSessionRepository.save(tableSession));
    }

    // ===========================
    // Cierre de sesión
    // ===========================
    @Override
    public TableSessionResponseDto closeCurrentSession() {
        Participant currentHost = tenantContext.requireParticipant();
        TableSession currentTs = tenantContext.requireTableSession();
        if (!currentTs.getSessionHost().getPublicId().equals(currentHost.getPublicId())) {
            throw new AccessDeniedException("Only the current host can end the session");
        }
        return closeSession(currentTs);
    }

    @Override
    public TableSessionResponseDto closeSessionById(UUID tableId) {
        TableSession tableSession = getTableSessionActiveByTable(tableId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
        return closeSession(tableSession);
    }

    private TableSessionResponseDto closeSession(TableSession tableSession) {
        List<Order> orders = tableSession.getOrders();
        boolean completedPayments = orders.stream()
                .map(o -> o.getPayment().getStatus())
                .allMatch(PaymentStatus.COMPLETED::equals);
        if (!completedPayments) {
            throw new InvalidPaymentStatusException("All payments must be paid to finish table session");
        }
        tableSession.setEndTime(LocalDateTime.now());
        diningTableService.updateStatus(DiningTableStatus.WAITING_RESET, tableSession.getDiningTable().getPublicId());
        log.debug("[TableSessionRepository] Calling save to close session {}", tableSession.getPublicId());
        tableSessionRepository.save(tableSession);
        return tableSessionMapper.toDto(tableSession);
    }

    // ===========================
    // Helpers internos
    // ===========================
    private TableSession createNewSession(DiningTable table, FoodVenue venue) {
        log.debug("[TableSession] Creating new session for table={}", table.getPublicId());
        TableSession tableSession = TableSession.builder()
                .diningTable(table)
                .foodVenue(venue)
                .startTime(LocalDateTime.now())
                .diningTable(table)
                .build();
        determineTableStatusPostSessionCreation(table, tableSession);
        log.debug("[TableSessionRepository] Calling save to create new session for table {}", table.getPublicId());
        tableSession = tableSessionRepository.save(tableSession);

        // Primer participante define host si corresponde (se carga al unir participante)
        return tableSession;
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
        determineTableStatusPostSessionCreation(ts.getDiningTable(), ts);
        log.debug("[TableSessionRepository] Calling save to update session {} after adding participant {}",
                ts.getPublicId(), p.getPublicId());

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
        determineTableStatusPostSessionCreation(ts.getDiningTable(), ts);
        log.debug("[TableSessionRepository] Calling save to update session {} after adding guest participant {}",
                ts.getPublicId(), p.getPublicId());
        tableSessionRepository.save(ts);
        return p;
    }

    private void migrateGuestToClient(TableSession ts, Participant guest, Participant client) {
        log.debug("[TableSession] Migrating guest {} to client {}", guest.getPublicId(), client.getPublicId());
        // Reasignar órdenes del guest al client
        ts.getOrders().stream()
                .filter(o -> o.getParticipant() != null && Objects.equals(o.getParticipant().getId(),
                        guest.getId()))
                .forEach(o -> o.setParticipant(client));

        // Remover guest de la sesión
        ts.getParticipants().remove(guest);
        participantService.softDelete(guest.getPublicId());
        log.debug("[TableSessionRepository] Calling save to update session {} after guest migration", ts.getPublicId());
        tableSessionRepository.save(ts);
    }

    private Optional<TableSession> getTableSessionActiveByTable(UUID tableId) {
        log.debug("[TableSessionRepository] Calling findTableSessionActiveByTable for tableId={}", tableId);
        return tableSessionRepository
                .findTableSessionByDiningTable_PublicIdAndDiningTableStatusAndEndTimeIsNull(
                        tableId, DiningTableStatus.IN_SESSION);
    }


    /**
     * Devuelve la sesión activa del usuario (si existe), usando primero el contexto y luego la DB.
     */
    private TableSession findActiveSessionForUser(User user) {
        // 1) Si el token ya trae sesión activa
        if (tenantContext.tableSessionOpt().filter(ts -> ts.getEndTime() == null).isPresent()) {
            return tenantContext.tableSessionOpt().orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
        }
        // 2) Query por email
        log.debug("[TableSessionRepository] Calling findActiveSessionByUserEmail for user email={}", user.getEmail());
        return tableSessionRepository.findActiveSessionByUserEmailAndDeletedFalse(user.getEmail()).orElse(null);
    }

    private AuthResponse signForParticipant(TableSession tableSession, Participant participant) {
        // SessionInfo completa para el token
        String role = participant.getRole().name();
        List<ParticipantResponseDto> participantsDto = tableSession.getParticipants().stream()
                .map(participantMapper::toResponseDto)
                .toList();

        SessionInfo si = SessionInfo.builder()
                .userId(participant.getUser() != null ? participant.getUser().getPublicId() : null)
                .subject(participant.getUser() != null ? participant.getUser().getEmail() : participant.getNickname())
                .foodVenueId(tableSession.getFoodVenue().getPublicId())
                .participantId(participant.getPublicId())
                .tableSessionId(tableSession.getPublicId())
                .role(role)
                .startTime(tableSession.getStartTime())
                .endTime(tableSession.getEndTime())
                .hostClient(participantMapper.toResponseDto(tableSession.getSessionHost()))
                .participants(participantsDto)
                .tableNumber(tableSession.getDiningTable().getNumber())
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

    private void determineTableStatusPostSessionCreation(DiningTable diningTable, TableSession tableSession) {

        if (diningTable.getCapacity().equals(tableSession.getParticipants().size())) {
            diningTableService.updateStatus(DiningTableStatus.COMPLETE, diningTable.getPublicId());
        } else {
            diningTableService.updateStatus(DiningTableStatus.IN_SESSION, diningTable.getPublicId());
        }
    }
}
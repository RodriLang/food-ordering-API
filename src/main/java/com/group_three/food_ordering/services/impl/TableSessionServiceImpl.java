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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static com.group_three.food_ordering.utils.EntityName.PARTICIPANT;
import static com.group_three.food_ordering.utils.EntityName.TABLE_SESSION;
import static com.group_three.food_ordering.utils.EntityName.DINING_TABLE;

@Slf4j
@Service
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
    @Transactional
    public AuthResponse enter(TableSessionRequestDto dto) {
        log.debug("[TableSession] Processing table session entry for tableId={}", dto.getTableId());

        DiningTable diningTable = diningTableService.getEntityById(dto.getTableId());
        FoodVenue venue = diningTable.getFoodVenue();

        log.debug("[TableSession] Getting context info");

        var userOpt = tenantContext.userOpt();
        log.debug("[TableSession] User present in context: {}", userOpt.isPresent());

        var partOpt = tenantContext.participantOpt();
        log.debug("[TableSession] Participant present in context: {}", partOpt.isPresent());

        var roleOpt = tenantContext.roleOpt();
        log.debug("[TableSession] Role present in context: {}", roleOpt.map(Enum::name).orElse("N/A"));

        var tsOpt = tenantContext.tableSessionOpt();
        log.debug("[TableSession] TableSession present in context: {}", tsOpt.isPresent() ? tsOpt.get().getPublicId() : "N/A");

        boolean isClient = roleOpt.map(r -> r == RoleType.ROLE_CLIENT).orElse(false);

        log.debug("[TableSession] Resolving flow: isClient={}, userId={}, participantId={}, tableSessionId={}",
                isClient, tenantContext.getUserId(), tenantContext.getParticipantId(), tenantContext.getTableSessionId());

        // Flujos:
        // 1) Idempotencia: cliente con sesión activa en el token
        if (isClient && tsOpt.isPresent() && tsOpt.get().getEndTime() == null) {
            log.debug("[TableSession] User has active session in token, reusing it");
            TableSession ts = tsOpt.get();
            assert userOpt.orElse(null) != null;
            Participant p = findOrCreateParticipantForUser(ts, userOpt.orElse(null));
            return signForParticipant(ts, p);
        }

        // 2) Sesión activa de la mesa (si existe)
        Optional<TableSession> activeForTableOpt = getTableSessionActiveByTable(diningTable.getPublicId());

        // 3) Cliente autenticado: priorizar su sesión activa; si no, reusar/crear en esta mesa
        if (isClient && userOpt.isPresent()) {
            log.debug("[TableSession] User is authenticated CLIENT, proceeding with client flow");
            User user = userOpt.get();

            TableSession userActive = findActiveSessionForUser(user);
            if (userActive != null) {
                log.debug("[TableSession] User has an active session {}, reusing it", userActive.getPublicId());
                Participant p = findOrCreateParticipantForUser(userActive, user);
                return signForParticipant(userActive, p);
            }

            TableSession ts = activeForTableOpt.orElseGet(() -> createNewSession(diningTable, venue));
            Participant client = findOrCreateParticipantForUser(ts, user);

            // Migración guest→client solo si ese guest pertenece a ESTA sesión
            partOpt.filter(g -> g.getRole() == RoleType.ROLE_GUEST)
                    .filter(g -> g.getTableSession() != null && ts.getId().equals(g.getTableSession().getId()))
                    .ifPresent(guest -> migrateGuestToClient(ts, guest, client));

            return signForParticipant(ts, client);
        }

        // 4) Invitado / anónimo
        log.debug("[TableSession]  User is GUEST or anonymous, proceeding with guest flow");
        TableSession ts = activeForTableOpt.orElseGet(() -> createNewSession(diningTable, venue));
        Participant guest = findOrCreateGuestParticipant(ts, partOpt.orElse(null));
        return signForParticipant(ts, guest);
    }


    // ===========================
    // Queries
    // ===========================

    @Override
    public Page<TableSessionResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
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
        TableSession tableSession = tableSessionRepository.findByParticipantsContains(List.of(current))
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPANT));
        return tableSessionMapper.toDto(tableSession);
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
        UUID foodVenueId = tenantContext.getFoodVenueId();
        return getByFoodVenueAndTable(foodVenueId, tableNumber, pageable);
    }

    @Override
    public Page<TableSessionResponseDto> getByTableAndTimeRange(
            Integer tableNumber,
            Instant start,
            Instant end,
            Pageable pageable
    ) {
        Instant effectiveEnd = (end == null) ? Instant.now() : end;
        if (start.isAfter(effectiveEnd)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndTableAndTimeRange for venueId={}, " +
                "tableNumber={}, range: {} to {}", foodVenueId, tableNumber, start, effectiveEnd);
        return tableSessionRepository
                .findByFoodVenuePublicIdAndDiningTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
                        foodVenueId, tableNumber, start, effectiveEnd, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getActiveSessions(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndEndTimeIsNull for venueId={}", foodVenueId);
        return tableSessionRepository.findByFoodVenuePublicIdAndEndTimeIsNull(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByHostClient(UUID clientId, Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[TableSessionRepository] Calling findByFoodVenueAndSessionHost for venueId={}, clientId={}",
                foodVenueId, clientId);
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(foodVenueId, clientId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByAuthUserHostClient(Pageable pageable) {
        // FIX: antes se usaba tableSessionId como "host"; debería ser el Participant host actual
        UUID hostParticipantId = tenantContext.getParticipantId();
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[TableSessionRepository] Calling findByFoodVenuePublicIdAndSessionHostPublicId for venueId={}, hostParticipantId={}", foodVenueId, hostParticipantId);
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(foodVenueId, hostParticipantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByParticipant(UUID participantId, Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[TableSessionRepository] Calling findPastSessionsByParticipant for venueId={}, participantId={}",
                foodVenueId, participantId);

        return tableSessionRepository.findPastSessionsByParticipantIdAndDeletedFalse(foodVenueId, participantId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByAuthUserParticipant(Pageable pageable) {
        UUID participantId = tenantContext.getParticipantId();
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
    @Transactional
    @Override
    public void closeCurrentSession() {
        log.debug("[TableSessionService] Closing current session by current host");
        Participant currentHost = tenantContext.requireParticipant();
        TableSession tableSession = tenantContext.requireTableSession();

        closeSession(tableSession);
    }

    @Transactional
    @Override
    public void closeSessionByTable(UUID tableId) {
        TableSession tableSession = getTableSessionActiveByTable(tableId)
                .orElseThrow(() -> new EntityNotFoundException(DINING_TABLE));

        closeSession(tableSession);
    }

    private void closeSession(TableSession tableSession) {
        // La entidad TableSession es 'managed' dentro de esta @Transactional,
        // por lo que cualquier cambio se persistirá automáticamente (dirty-checking) al final.

        validatePaymentsForOrders(tableSession.getOrders());
        log.debug("[TableSessionService] All payments are COMPLETED for tableSessionId={}",
                tableSession.getPublicId());

        // 1. Mutaciones a la entidad managed
        tableSession.setEndTime(Instant.now());

        tableSession.getDiningTable().setStatus(DiningTableStatus.WAITING_RESET);
    }

    private void validatePaymentsForOrders(List<Order> orders) {

        log.debug("[TableSessionService] Validating payment status for {} orders", orders.size());

        if (!orders.stream()
                .map(o -> o.getPayment().getStatus())
                .allMatch(PaymentStatus.COMPLETED::equals)) {
            throw new InvalidPaymentStatusException("All payments must be paid to finish table session");
        }
    }

    // ===========================
    // Helpers internos
    // ===========================
    private TableSession createNewSession(DiningTable table, FoodVenue venue) {
        log.debug("[TableSession] Creating new session for table={}", table.getPublicId());
        TableSession tableSession = TableSession.builder()
                .diningTable(table)
                .foodVenue(venue)
                .startTime(Instant.now())
                .diningTable(table)
                .build();

        log.debug("[TableSessionRepository] Calling save to create new session for table {}", table.getPublicId());
        updateTableStatusIfNeeded(table, 0 /*participantsCountAfter*/);

        return tableSessionRepository.save(tableSession);
    }

    private Participant findOrCreateParticipantForUser(TableSession ts, User user) {
        log.debug("[TableSessionService] Searching for existing participant for user {} in session {}", user.getPublicId(), ts.getPublicId());
        return ts.getParticipants().stream()
                .filter(p -> p.getUser() != null && Objects.equals(p.getUser().getId(), user.getId()))
                .findFirst()
                .orElseGet(() -> {
                    Participant p = participantService.create(user, ts); // ROLE_CLIENT, nickname, etc.
                    if (ts.getSessionHost() == null) ts.setSessionHost(p);
                    ts.getParticipants().add(p);

                    // calcular status con el tamaño definitivo
                    updateTableStatusIfNeeded(ts.getDiningTable(), ts.getParticipants().size());

                    // un solo save
                    log.debug("[TableSessionRepository] Calling save to persist new client participant {} and update session {}", p.getPublicId(), ts.getPublicId());
                    tableSessionRepository.save(ts);
                    return p;
                });
    }

    private Participant findOrCreateGuestParticipant(TableSession tableSession, Participant currentGuestFromCtx) {
        // Reusar si el token ya trae participant guest de esta sesión
        if (currentGuestFromCtx != null
                && currentGuestFromCtx.getRole() == RoleType.ROLE_GUEST
                && currentGuestFromCtx.getTableSession() != null
                && Objects.equals(currentGuestFromCtx.getTableSession().getId(), tableSession.getId())) {
            log.debug("[TableSessionService] Reusing existing guest participant {}", currentGuestFromCtx.getPublicId());
            return currentGuestFromCtx;
        }
        Participant participant = participantService.create(null, tableSession); // ROLE_GUEST
        if (tableSession.getSessionHost() == null) {
            tableSession.setSessionHost(participant); // si es el primero, queda como host
        }

        tableSession.getParticipants().add(participant);

        updateTableStatusIfNeeded(tableSession.getDiningTable(), tableSession.getParticipants().size());

        log.debug("[TableSessionRepository] Calling save to update session {} after adding guest participant {}",
                tableSession.getPublicId(), participant.getPublicId());
        tableSessionRepository.save(tableSession);
        return participant;
    }

    private void migrateGuestToClient(TableSession tableSession, Participant guest, Participant client) {
        log.debug("[TableSession] Migrating guest {} to client {}", guest.getPublicId(), client.getPublicId());
        // Reasignar órdenes del guest al client
        tableSession.getOrders().stream()
                .filter(order -> order.getParticipant() != null && Objects.equals(order.getParticipant().getId(),
                        guest.getId()))
                .forEach(order -> order.setParticipant(client));
        
        // Remover guest de la sesión
        tableSession.getParticipants().remove(guest);
        participantService.softDelete(guest.getPublicId());

        updateTableStatusIfNeeded(tableSession.getDiningTable(), tableSession.getParticipants().size());
        log.debug("[TableSessionRepository] Calling save to update session {} after guest migration", tableSession.getPublicId());
        tableSessionRepository.save(tableSession);
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
        if (tenantContext.tableSessionOpt().filter(tableSession -> tableSession.getEndTime() == null).isPresent()) {
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
                .role(role)
                .build();
    }

    private void determineTableStatusPostSessionCreation(DiningTable diningTable, TableSession tableSession) {

        if (diningTable.getCapacity().equals(tableSession.getParticipants().size())) {
            diningTableService.updateStatus(DiningTableStatus.COMPLETE, diningTable.getPublicId());
        } else {
            diningTableService.updateStatus(DiningTableStatus.IN_SESSION, diningTable.getPublicId());
        }
    }

    private void updateTableStatusIfNeeded(DiningTable table, int participantsCount) {
        DiningTableStatus newStatus =
                table.getCapacity().equals(participantsCount)
                        ? DiningTableStatus.COMPLETE
                        : DiningTableStatus.IN_SESSION;

        if (table.getStatus() != newStatus) {
            table.setStatus(newStatus);
            log.debug("[DiningTableService] Calling save to update status to {} for table {}", newStatus, table.getPublicId());
            diningTableService.save(table);
        }
    }
}

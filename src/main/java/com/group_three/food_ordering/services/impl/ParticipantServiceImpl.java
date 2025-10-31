package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.utils.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.PARTICIPANT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final TenantContext tenantContext;

    @Override
    public Participant create(User user, TableSession tableSession) {

        Participant participant = Participant.builder()
                .publicId(UUID.randomUUID())
                .tableSession(tableSession)
                .nickname((user != null) ? user.getName() : NicknameGenerator.generateRandomNickname())
                .role(user != null ? RoleType.ROLE_CLIENT : RoleType.ROLE_GUEST)
                .user(user)
                .build();

        log.debug("[ParticipantRepository] Calling save to create new participant for session {}",
                tableSession.getPublicId());

        participantRepository.save(participant);
        log.debug("[ParticipantService] Participant created. Nickname={}. Role={}. User={}",
                participant.getNickname(), participant.getRole(), user != null ? user.getEmail() : null);

        return participant;
    }

    @Override
    public Participant update(Participant participant, User user) {
        if (user != null) {
            participant.setUser(user);
            participant.setRole(RoleType.ROLE_CLIENT);
            participant.setNickname(user.getName());
        }
        log.debug("[ParticipantRepository] Calling save to update participant {}", participant.getPublicId());
        participantRepository.save(participant);
        log.debug("[ParticipantService] Participant updated. Nickname={}. Role={}. User={}",
                participant.getNickname(), participant.getRole(), user != null ? user.getEmail() : null);
        return participant;
    }

    @Override
    public ParticipantResponseDto getById(UUID id) {
        Participant participant = getEntityById(id);
        return participantMapper.toResponseDto(participant);
    }

    @Override
    public Participant getEntityById(UUID id) {
        log.debug("[ParticipantRepository] Calling findByPublicId for participantId={}", id);
        return participantRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPANT, id.toString()));
    }

    @Override
    public AuthResponse delegateHostingDuties(UUID participantId) {
        Participant hostDesigned = getEntityById(participantId);
        Participant currentHost = tenantContext.requireParticipant();
        TableSession currentTableSession = tenantContext.requireTableSession();
        if (!currentTableSession.getSessionHost().getPublicId().equals(currentHost.getPublicId())) {
            throw new AccessDeniedException("Only the current host can delegate hosting duties");
        }
        //implementar una forma de capturar este evento para avisar al nuevo hot
        log.debug("[ParticipantService] Delegating host duties from {} to {} in session {}",
                currentHost.getPublicId(), hostDesigned.getPublicId(), currentTableSession.getPublicId());

        currentTableSession.setSessionHost(hostDesigned);

        // Nota: Asumo que la TableSession será guardada en un servicio de nivel superior o que el objeto TableSession
        // es administrado y persistido por otro servicio/transacción, como TableSessionService.
        // Si TableSession es un objeto anidado o no se guarda aquí, el log es suficiente.

        return null;
    }

    @Override
    public void softDelete(UUID participantId) {
        Participant participant = getEntityById(participantId);
        participant.setDeleted(false);
        log.debug("[ParticipantRepository] Calling save to soft delete participant {}", participantId);
        participantRepository.save(participant);
    }
}
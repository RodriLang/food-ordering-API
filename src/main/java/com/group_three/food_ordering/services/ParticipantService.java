package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;

import java.util.UUID;

public interface ParticipantService {

    Participant create(User user, TableSession tableSession);

    Participant update(UUID participantIdUser, User user);

    ParticipantResponseDto getById(UUID id);

    Participant getEntityById(UUID id);

    AuthResponse delegateHostingDuties(UUID participantId);
}
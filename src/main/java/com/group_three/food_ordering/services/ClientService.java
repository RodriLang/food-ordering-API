package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.create.ClientCreateDto;
import com.group_three.food_ordering.dto.update.ClientPatchDto;
import com.group_three.food_ordering.dto.update.ClientUpdateDto;
import com.group_three.food_ordering.dto.response.ClientResponseDto;
import com.group_three.food_ordering.models.Participant;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    ClientResponseDto create(ClientCreateDto clientCreateDto);

    List<ClientResponseDto> getAll();

    ClientResponseDto getById(UUID id);

    void delete(UUID id);

    ClientResponseDto update(UUID id, ClientUpdateDto clientUpdateDto);

    Participant getEntityById(UUID id);

    ClientResponseDto replace(UUID id, ClientUpdateDto dto);

    ClientResponseDto partialUpdate(UUID id, ClientPatchDto dto);

}

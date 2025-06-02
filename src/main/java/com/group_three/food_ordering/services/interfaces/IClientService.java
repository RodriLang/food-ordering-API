package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.update.ClientPatchDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.models.Client;

import java.util.List;
import java.util.UUID;

public interface IClientService {

    ClientResponseDto create(ClientCreateDto clientCreateDto);

    List<ClientResponseDto> getAll();

    ClientResponseDto getById(UUID id);

    void delete(UUID id);

    ClientResponseDto update(UUID id, ClientUpdateDto clientUpdateDto);

    Client getEntityById(UUID id);

    ClientResponseDto replace(UUID id, ClientUpdateDto dto);

    ClientResponseDto partialUpdate(UUID id, ClientPatchDto dto);

}

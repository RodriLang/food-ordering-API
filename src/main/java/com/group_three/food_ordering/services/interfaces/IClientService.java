package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.models.Client;

import java.util.List;
import java.util.UUID;

public interface IClientService {
    ClientResponseDto create(ClientCreateDto clientCreateDto);
    List<ClientResponseDto> getAll();
    ClientResponseDto getById(Long id);
    void delete(Long id);
    ClientResponseDto update(Long id, ClientCreateDto clientCreateDto);
    Client getEntityById(UUID id);
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.services.interfaces.IClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientServiceImpl implements IClientService {
    @Override
    public ClientResponseDto create(ClientCreateDto clientCreateDto) {
        return null;
    }

    @Override
    public List<ClientResponseDto> getAll() {
        return List.of();
    }

    @Override
    public ClientResponseDto getById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public ClientResponseDto update(Long id, ClientCreateDto clientCreateDto) {
        return null;
    }

    @Override
    public Client getEntityById(UUID id) {
        return new Client();
    }
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.exceptions.ClientNotFoundException;
import com.group_three.food_ordering.mappers.ClientMapper;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.UserEntity;
import com.group_three.food_ordering.repositories.IClientRepository;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.services.interfaces.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final IUserRepository userRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDto create(ClientCreateDto dto) {
        Client client = clientMapper.toEntity(dto);

        if (dto.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ClientNotFoundException("UserEntity with ID " + dto.getUserId() + " not found"));
            client.setUserEntity(userEntity);
        } else if (dto.getUser() != null) {
            UserEntity userEntity = clientMapper.toUser(dto.getUser());
            userEntity.setRole(dto.getUser().getRole());
            userRepository.save(userEntity);
            client.setUserEntity(userEntity);
        }

        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(savedClient);
    }

    @Override
    public List<ClientResponseDto> getAll() {
        return clientRepository.findAllByUserEntity_RemovedAtIsNull().stream()
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDto getById(UUID id) {
        Client client = clientRepository.findByIdAndUserEntity_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
        return clientMapper.toResponseDto(client);
    }

    @Override
    public void delete(UUID id) {
        Client client = clientRepository.findByIdAndUserEntity_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
        client.getUserEntity().setRemovedAt(LocalDateTime.now());
        userRepository.save(client.getUserEntity());
    }

    @Override
    public ClientResponseDto update(UUID id, ClientUpdateDto dto) {
        Client client = clientRepository.findByIdAndUserEntity_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

        clientMapper.updateClientFromDto(dto, client);
        clientMapper.updateUserFromDto(dto.getUser(), client.getUserEntity());

        Client updatedClient = clientRepository.save(client);
        userRepository.save(client.getUserEntity());

        return clientMapper.toResponseDto(updatedClient);
    }

    @Override
    public Client getEntityById(UUID id) {
        return clientRepository.findByIdAndUserEntity_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
    }
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.dtos.update.AddressUpdateDto;
import com.group_three.food_ordering.dtos.update.ClientPatchDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.dtos.update.UserPatchDto;
import com.group_three.food_ordering.exceptions.ClientNotFoundException;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.mappers.ClientMapper;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.repositories.IClientRepository;
import com.group_three.food_ordering.services.interfaces.IClientService;
import com.group_three.food_ordering.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {

    private final IClientRepository clientRepository;
    private final IUserService userService;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDto create(ClientCreateDto dto) {
        Client client = clientMapper.toEntity(dto);

        /// Caso 1: Se proporciona un User ID existente
        if (dto.getUserId() != null) {
            User userEntity = userService.getEntityById(dto.getUserId());
            client.setUser(userEntity);
        }

        /// Caso 2: Se proporciona un User embebido
        else if (dto.getUser() != null) {
            if (userService.getAll().stream().anyMatch(u -> u.getEmail().equals(dto.getUser().getEmail()))) {
                throw new EmailAlreadyUsedException(dto.getUser().getEmail());
            }

            /// Asignar el rol automáticamente como CLIENTE registrado
            dto.getUser().setRole(RoleType.ROLE_CLIENT);

            User userEntity = userService.createIfPresent(dto.getUser());
            client.setUser(userEntity);
        }

        /// Caso 3: Invitado (user == null)
        else {
            User guest = User.builder()
                    .name("Invitado")
                    .lastName("Invitado")
                    .email(generateUniqueGuestEmail())
                    .password("guest") // opcional: podrías encriptarlo
                    .birthDate(LocalDateTime.now().toLocalDate())
                    .phone("-")
                    .createdAt(LocalDateTime.now())
                    .role(RoleType.ROLE_GUEST)
                    .build();

            User savedGuest = userService.createIfPresent(toDtoFromEntity(guest));
            client.setUser(savedGuest);

            if (dto.getNickname() == null || dto.getNickname().isBlank()) {
                client.setNickname("Invitado");
            }
        }

        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(savedClient);
    }


    private String generateUniqueGuestEmail() {
        String base = "guest";
        String domain = "@guest.local";
        String email;
        int counter = 1;
        do {
            email = base + counter + domain;
            counter++;
        } while (clientRepository.existsByUser_Email(email));
        return email;
    }

    /// Metodo auxiliar para convertir User a UserCreateDto para invitado
    private com.group_three.food_ordering.dtos.create.UserCreateDto toDtoFromEntity(User user) {
        return com.group_three.food_ordering.dtos.create.UserCreateDto.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .birthDate(user.getBirthDate())
                .phone(user.getPhone())
                .role(user.getRole())
                .address(null)
                .build();
    }

    @Override
    public List<ClientResponseDto> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDto getById(UUID id) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
        return clientMapper.toResponseDto(client);
    }

    @Override
    public void delete(UUID id) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
        client.getUser().setRemovedAt(LocalDateTime.now());
        userService.delete(client.getUser().getId());
    }

    @Override
    public ClientResponseDto update(UUID id, ClientUpdateDto dto) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

        clientMapper.updateClientFromDto(dto, client);
        clientMapper.updateUserFromDto(dto.getUser(), client.getUser());

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(updatedClient);
    }

    @Override
    public Client getEntityById(UUID id) {
        return clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));
    }

    @Override
    public ClientResponseDto replace(UUID id, ClientUpdateDto dto) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

        clientMapper.updateClientFromDto(dto, client);
        clientMapper.updateUserFromDto(dto.getUser(), client.getUser());

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(updatedClient);
    }

    @Override
    public ClientResponseDto partialUpdate(UUID id, ClientPatchDto dto) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id: " + id));

        if (dto.getUser() != null) {
            User user = client.getUser();
            UserPatchDto userDto = dto.getUser();

            if (userDto.getName() != null) user.setName(userDto.getName());
            if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());
            if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
            if (userDto.getBirthDate() != null) user.setBirthDate(userDto.getBirthDate());

            if (userDto.getAddress() != null && user.getAddress() != null) {
                AddressUpdateDto addr = userDto.getAddress();

                if (addr.getStreet() != null) user.getAddress().setStreet(addr.getStreet());
                if (addr.getNumber() != null) user.getAddress().setNumber(addr.getNumber());
                if (addr.getCity() != null) user.getAddress().setCity(addr.getCity());
                if (addr.getProvince() != null) user.getAddress().setProvince(addr.getProvince());
                if (addr.getPostalCode() != null) user.getAddress().setPostalCode(addr.getPostalCode());
                if (addr.getCountry() != null) user.getAddress().setCountry(addr.getCountry());
            }
        }

        Client updated = clientRepository.save(client);
        return clientMapper.toResponseDto(updated);
    }
}

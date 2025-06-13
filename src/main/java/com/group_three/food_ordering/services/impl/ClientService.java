package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.dtos.update.AddressUpdateDto;
import com.group_three.food_ordering.dtos.update.ClientPatchDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.dtos.update.UserPatchDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.mappers.ClientMapper;
import com.group_three.food_ordering.models.Address;
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
                .toList();
    }

    @Override
    public ClientResponseDto getById(UUID id) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client" + id));
        return clientMapper.toResponseDto(client);
    }

    @Override
    public void delete(UUID id) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client" + id));
        client.getUser().setRemovedAt(LocalDateTime.now());
        userService.delete(client.getUser().getId());
    }

    @Override
    public ClientResponseDto update(UUID id, ClientUpdateDto dto) {
        Client client = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client" + id));

        clientMapper.updateClientFromDto(dto, client);
        clientMapper.updateUserFromDto(dto.getUser(), client.getUser());

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(updatedClient);
    }

    @Override
    public Client getEntityById(UUID id) {
        return clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client" + id));
    }

    @Override
    public ClientResponseDto replace(UUID id, ClientUpdateDto dto) {
        Client client = this.findClientById(id);

        clientMapper.updateClientFromDto(dto, client);
        clientMapper.updateUserFromDto(dto.getUser(), client.getUser());

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponseDto(updatedClient);
    }

    @Override
    public ClientResponseDto partialUpdate(UUID id, ClientPatchDto dto) {
        Client client = this.findClientById(id);

        if (dto.getUser() != null) {
            updateUserFields(client.getUser(), dto.getUser());
        }

        Client updated = clientRepository.save(client);
        return clientMapper.toResponseDto(updated);
    }

    private void updateUserFields(User user, UserPatchDto userDto) {
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        if (userDto.getBirthDate() != null) user.setBirthDate(userDto.getBirthDate());

        if (userDto.getAddress() != null && user.getAddress() != null) {
            updateAddressFields(user.getAddress(), userDto.getAddress());
        }
    }

    private void updateAddressFields(Address address, AddressUpdateDto addr) {
        if (addr.getStreet() != null) address.setStreet(addr.getStreet());
        if (addr.getNumber() != null) address.setNumber(addr.getNumber());
        if (addr.getCity() != null) address.setCity(addr.getCity());
        if (addr.getProvince() != null) address.setProvince(addr.getProvince());
        if (addr.getPostalCode() != null) address.setPostalCode(addr.getPostalCode());
        if (addr.getCountry() != null) address.setCountry(addr.getCountry());
    }


    private Client findClientById(UUID id) {
        return clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client" + id));
    }

}
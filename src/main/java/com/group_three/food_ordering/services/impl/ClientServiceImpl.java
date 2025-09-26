package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.create.ClientCreateDto;
import com.group_three.food_ordering.dto.response.ClientResponseDto;
import com.group_three.food_ordering.dto.update.AddressUpdateDto;
import com.group_three.food_ordering.dto.update.ClientPatchDto;
import com.group_three.food_ordering.dto.update.ClientUpdateDto;
import com.group_three.food_ordering.dto.update.UserPatchDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.mappers.ClientMapper;
import com.group_three.food_ordering.models.Address;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.repositories.ClientRepository;
import com.group_three.food_ordering.services.ClientService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserService userService;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponseDto create(ClientCreateDto dto) {
        Participant participant = clientMapper.toEntity(dto);

        /// Caso 1: Se proporciona un User ID existente
        if (dto.getUserId() != null) {
            User userEntity = userService.getEntityById(dto.getUserId());
            participant.setUser(userEntity);
        }

        /// Caso 2: Se proporciona un User embebido
        else if (dto.getUser() != null) {
            if (userService.getAll().stream().anyMatch(u -> u.getEmail().equals(dto.getUser().getEmail()))) {
                throw new EmailAlreadyUsedException(dto.getUser().getEmail());
            }

            /// Asignar el rol automáticamente como CLIENTE registrado
            dto.getUser().setRole(RoleType.ROLE_CLIENT);

            User userEntity = userService.createIfPresent(dto.getUser());
            participant.setUser(userEntity);
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
            participant.setUser(savedGuest);

            if (dto.getNickname() == null || dto.getNickname().isBlank()) {
                participant.setNickname("Invitado");
            }
        }

        Participant savedParticipant = clientRepository.save(participant);
        return clientMapper.toResponseDto(savedParticipant);
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
    private UserCreateDto toDtoFromEntity(User user) {
        return UserCreateDto.builder()
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
        Participant participant = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
        return clientMapper.toResponseDto(participant);
    }

    @Override
    public void delete(UUID id) {
        Participant participant = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
        participant.getUser().setRemovedAt(LocalDateTime.now());
        userService.delete(participant.getUser().getId());
    }

    @Override
    public ClientResponseDto update(UUID id, ClientUpdateDto dto) {
        Participant participant = clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));

        clientMapper.updateClientFromDto(dto, participant);
        clientMapper.updateUserFromDto(dto.getUser(), participant.getUser());

        Participant updatedParticipant = clientRepository.save(participant);
        return clientMapper.toResponseDto(updatedParticipant);
    }

    @Override
    public Participant getEntityById(UUID id) {
        return clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
    }

    @Override
    public ClientResponseDto replace(UUID id, ClientUpdateDto dto) {
        Participant participant = this.findClientById(id);

        clientMapper.updateClientFromDto(dto, participant);
        clientMapper.updateUserFromDto(dto.getUser(), participant.getUser());

        Participant updatedParticipant = clientRepository.save(participant);
        return clientMapper.toResponseDto(updatedParticipant);
    }

    @Override
    public ClientResponseDto partialUpdate(UUID id, ClientPatchDto dto) {
        Participant participant = this.findClientById(id);

        if (dto.getUser() != null) {
            updateUserFields(participant.getUser(), dto.getUser());
        }

        Participant updated = clientRepository.save(participant);
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


    private Participant findClientById(UUID id) {
        return clientRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
    }

}
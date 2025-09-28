package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.update.AddressUpdateDto;
import com.group_three.food_ordering.dto.update.ParticipantPatchDto;
import com.group_three.food_ordering.dto.update.ParticipantUpdateDto;
import com.group_three.food_ordering.dto.update.UserPatchDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.models.Address;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserService userService;
    private final ParticipantMapper participantMapper;



    private String generateUniqueGuestEmail() {
        String base = "guest";
        String domain = "@guest.local";
        String email;
        int counter = 1;
        do {
            email = base + counter + domain;
            counter++;
        } while (participantRepository.existsByUser_Email(email));
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
    public Participant create(User user, TableSession tableSession) {

        Participant participant = Participant.builder()
                .tableSession(tableSession)
                .nickname((user != null) ? user.getName() : "Guest" + System.nanoTime())
                .role((user != null) ? user.getRole() : RoleType.ROLE_GUEST)
                .user(user)
                .build();

        participantRepository.save(participant);

        return participant;
    }

    @Override
    public List<ParticipantResponseDto> getAll() {
        return participantRepository.findAll().stream()
                .map(participantMapper::toResponseDto)
                .toList();
    }

    @Override
    public ParticipantResponseDto getById(UUID id) {
        Participant participant = participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
        return participantMapper.toResponseDto(participant);
    }

    @Override
    public void delete(UUID id) {
        Participant participant = participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
        participant.getUser().setRemovedAt(LocalDateTime.now());
        userService.delete(participant.getUser().getId());
    }

    @Override
    public ParticipantResponseDto update(UUID id, ParticipantUpdateDto dto) {
        Participant participant = participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));

        participantMapper.updateClientFromDto(dto, participant);
        participantMapper.updateUserFromDto(dto.getUser(), participant.getUser());

        Participant updatedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseDto(updatedParticipant);
    }

    @Override
    public Participant getEntityById(UUID id) {
        return participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
    }

    @Override
    public ParticipantResponseDto replace(UUID id, ParticipantUpdateDto dto) {
        Participant participant = this.findClientById(id);

        participantMapper.updateClientFromDto(dto, participant);
        participantMapper.updateUserFromDto(dto.getUser(), participant.getUser());

        Participant updatedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseDto(updatedParticipant);
    }

    @Override
    public ParticipantResponseDto partialUpdate(UUID id, ParticipantPatchDto dto) {
        Participant participant = this.findClientById(id);

        if (dto.getUser() != null) {
            updateUserFields(participant.getUser(), dto.getUser());
        }

        Participant updated = participantRepository.save(participant);
        return participantMapper.toResponseDto(updated);
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
        return participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Client", id.toString()));
    }

}
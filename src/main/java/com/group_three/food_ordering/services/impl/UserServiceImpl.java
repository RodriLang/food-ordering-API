package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.AddressMapper;
import com.group_three.food_ordering.mappers.UserMapper;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AddressMapper addressMapper;
    private final AuthService authService;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }

        User userEntity = userMapper.toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setCreatedAt(LocalDateTime.now());

        return userMapper.toResponseDto(userRepository.save(userEntity));
    }

    public User createIfPresent(UserCreateDto dto) {
        if (dto == null) return null;

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }

        User userEntity = userMapper.toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setCreatedAt(LocalDateTime.now());

        return userRepository.save(userEntity);
    }

    @Override
    public UserResponseDto getAuthenticatedUser() {
        User authUser = authService.getCurrentUser().orElseThrow(() ->
                new EntityNotFoundException("Authenticated User"));
        return userMapper.toResponseDto(authUser);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        User userEntity = this.getEntityById(id);
        return userMapper.toResponseDto(userEntity);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> getActiveUsers() {
        return userRepository.findAllByRemovedAtIsNull()
                .stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<UserResponseDto> getDeletedUsers() {
        return userRepository.findAllByRemovedAtIsNotNull()
                .stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto update(UUID id, UserUpdateDto dto) {
        User userEntity = this.getEntityById(id);

        if (!userEntity.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }

        userMapper.updateEntity(dto, userEntity);

        if (dto.getAddress() != null) {
            if (userEntity.getAddress() == null) {
                userEntity.setAddress(addressMapper.toEntity(dto.getAddress()));
            } else {
                addressMapper.updateEntity(dto.getAddress(), userEntity.getAddress());
            }
        }

        return userMapper.toResponseDto(userRepository.save(userEntity));
    }

    @Override
    public void delete(UUID id) {
        User userEntity = this.getEntityById(id);

        userEntity.setRemovedAt(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    @Override
    public User getEntityById(UUID id) {
        return userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id.toString()));
    }
}

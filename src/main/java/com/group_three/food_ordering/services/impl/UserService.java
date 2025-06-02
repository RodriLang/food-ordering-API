package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.exceptions.UserNotFoundException;
import com.group_three.food_ordering.mappers.AddressMapper;
import com.group_three.food_ordering.mappers.UserMapper;
import com.group_three.food_ordering.models.UserEntity;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AddressMapper addressMapper;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }

        UserEntity userEntity = userMapper.toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setCreatedAt(LocalDateTime.now());

        return userMapper.toResponseDto(userRepository.save(userEntity));
    }

    public UserEntity createIfPresent(UserCreateDto dto) {
        if (dto == null) return null;

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }

        UserEntity userEntity = userMapper.toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setCreatedAt(LocalDateTime.now());

        return userRepository.save(userEntity);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        UserEntity userEntity = userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));
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
        UserEntity userEntity = userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));

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
        UserEntity userEntity = userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userEntity.setRemovedAt(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    @Override
    public UserEntity getEntityById(UUID id) {
        return userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.exceptions.UserNotFoundException;
import com.group_three.food_ordering.mappers.AddressMapper;
import com.group_three.food_ordering.mappers.UserMapper;
import com.group_three.food_ordering.models.User;
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

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getById(UUID id) {
        User user = userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDto(user);
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
        User user = userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }

        userMapper.updateEntity(dto, user);

        if (dto.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(addressMapper.toEntity(dto.getAddress()));
            } else {
                addressMapper.updateEntity(dto.getAddress(), user.getAddress());
            }
        }

        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findByIdAndRemovedAtIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setRemovedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}

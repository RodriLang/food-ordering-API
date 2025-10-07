package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.UserRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AddressMapper addressMapper;
    private final AuthService authService;

    private static final String ENTITY_NAME = "User";
    private static final String AUTH_ENTITY_NAME = "Authenticated User";

    @Override
    public UserResponseDto create(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }
        User userEntity = userMapper.toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setPublicId(UUID.randomUUID());
        return userMapper.toResponseDto(userRepository.save(userEntity));
    }

    @Override
    public UserResponseDto getAuthenticatedUser() {
        User authUser = authService.getAuthUser().orElseThrow(() ->
                new EntityNotFoundException(AUTH_ENTITY_NAME));
        return userMapper.toResponseDto(authUser);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        User userEntity = this.getEntityById(id);
        return userMapper.toResponseDto(userEntity);
    }

    @Override
    public Page<UserResponseDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDto);
    }

    @Override
    public Page<UserResponseDto> getActiveUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponseDto);
    }

    @Override
    public Page<UserResponseDto> getDeletedUsers(Pageable pageable) {
        return userRepository.findAllDeleted(pageable)
                .map(userMapper::toResponseDto);
    }

    @Override
    public UserResponseDto updateUser(UUID id, com.group_three.food_ordering.dto.request.UserRequestDto dto) {
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
    public UserResponseDto updateAuthUser(UserRequestDto dto) {
        UUID authUserId = authService.determineAuthUser().getPublicId();
        return updateUser(authUserId, dto);
    }

    @Override
    public void deleteUser(UUID id) {
        User userEntity = this.getEntityById(id);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteAuthUser() {
        UUID authUserId = authService.determineAuthUser().getPublicId();
        deleteUser(authUserId);
    }

    @Override
    public User getEntityById(UUID id) {
        return userRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id.toString()));
    }
}

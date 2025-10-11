package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.RequestContext;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.AddressMapper;
import com.group_three.food_ordering.mappers.UserMapper;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.repositories.UserRepository;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import static com.group_three.food_ordering.utils.EntityName.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AddressMapper addressMapper;
    private final RequestContext requestContext;
    private final EmploymentRepository employmentRepository;

    @Override
    public UserDetailResponseDto create(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }
        User userEntity = userMapper.toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setPublicId(UUID.randomUUID());
        return userMapper.toDetailResponseDto(userRepository.save(userEntity));
    }

    @Override
    public UserDetailResponseDto getAuthenticatedUser() {
        User authUser = requestContext.requireUser();
        return userMapper.toDetailResponseDto(authUser);
    }

    @Override
    public UserDetailResponseDto getById(UUID id) {
        User userEntity = this.getEntityById(id);
        return userMapper.toDetailResponseDto(userEntity);
    }

    @Override
    public Page<UserDetailResponseDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDetailResponseDto);
    }

    @Override
    public Page<UserDetailResponseDto> getActiveUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDetailResponseDto);
    }

    @Override
    public Page<UserDetailResponseDto> getDeletedUsers(Pageable pageable) {
        return userRepository.findAllDeleted(pageable)
                .map(userMapper::toDetailResponseDto);
    }

    @Override
    public UserDetailResponseDto updateUser(UUID id, com.group_three.food_ordering.dto.request.UserRequestDto dto) {
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

        return userMapper.toDetailResponseDto(userRepository.save(userEntity));
    }

    @Override
    public UserDetailResponseDto updateAuthUser(UserRequestDto dto) {
        UUID authUserId = requestContext.requireUser().getPublicId();
        return updateUser(authUserId, dto);
    }

    @Override
    public void deleteUser(UUID id) {
        User userEntity = this.getEntityById(id);
        userEntity.setDeleted(Boolean.TRUE);
        userEntity.getEmployments().forEach(employment -> employment.setDeleted(Boolean.TRUE));
        employmentRepository.saveAll(userEntity.getEmployments());
        userRepository.save(userEntity);
    }

    @Override
    public void deleteAuthUser() {
        UUID authUserId = requestContext.requireUser().getPublicId();
        deleteUser(authUserId);
    }

    @Override
    public User getEntityById(UUID id) {
        return userRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(USER, id.toString()));
    }
}

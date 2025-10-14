package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import static com.group_three.food_ordering.utils.EntityName.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;
    private final TenantContext tenantContext;
    private final EmploymentRepository employmentRepository;

    @Override
    public UserDetailResponseDto create(UserRequestDto dto) {
        log.info("[UserRepository] Reading Database existsByEmail: {}", dto.getEmail());
        if (userRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
            throw new EmailAlreadyUsedException(dto.getEmail());
        }
        User userEntity = userMapper.toEntity(dto);
        userEntity.setPublicId(UUID.randomUUID());
        log.info("[UserRepository] Saving user: {}", userEntity.getEmail());
        return userMapper.toDetailResponseDto(userRepository.save(userEntity));
    }

    @Override
    public UserDetailResponseDto getAuthenticatedUser() {
        User authUser = tenantContext.requireUser();
        return userMapper.toDetailResponseDto(authUser);
    }

    @Override
    public UserDetailResponseDto getById(UUID id) {
        User userEntity = this.getEntityById(id);
        return userMapper.toDetailResponseDto(userEntity);
    }

    @Override
    public Page<UserDetailResponseDto> getAll(Pageable pageable) {
        log.info("[UserRepository] Reading Database findAll");
        return userRepository.findAll(pageable)
                .map(userMapper::toDetailResponseDto);
    }

    @Override
    public Page<UserDetailResponseDto> getActiveUsers(Pageable pageable) {
        log.info("[UserRepository] Reading Database findAllByDeletedFalse");
        return userRepository.findAllByDeletedFalse(pageable)
                .map(userMapper::toDetailResponseDto);
    }

    @Override
    public Page<UserDetailResponseDto> getDeletedUsers(Pageable pageable) {
        log.info("[UserRepository] Reading Database findAllByDeletedTrue");
        return userRepository.findAllDeleted(pageable)
                .map(userMapper::toDetailResponseDto);
    }

    @Override
    public UserDetailResponseDto updateUser(UUID id, com.group_three.food_ordering.dto.request.UserRequestDto dto) {
        User userEntity = this.getEntityById(id);
        log.info("[UserRepository] Reading Database for update existsByEmail: {}", dto.getEmail());
        if (!userEntity.getEmail().equals(dto.getEmail()) && userRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
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
        log.info("[UserRepository] Updating user");
        return userMapper.toDetailResponseDto(userRepository.save(userEntity));
    }

    @Override
    public UserDetailResponseDto updateAuthUser(UserRequestDto dto) {
        UUID authUserId = tenantContext.getUserId();
        return updateUser(authUserId, dto);
    }

    @Override
    public void deleteUser(UUID id) {
        User userEntity = this.getEntityById(id);
        userEntity.setDeleted(Boolean.TRUE);
        userEntity.getEmployments().forEach(employment -> employment.setDeleted(Boolean.TRUE));
        log.info("[EmploymentRepository] Deleting user employments");
        employmentRepository.saveAll(userEntity.getEmployments());
        log.info("[UserRepository] Deleting user");
        userRepository.save(userEntity);
    }

    @Override
    public void deleteAuthUser() {
        UUID authUserId = tenantContext.getUserId();
        deleteUser(authUserId);
    }

    @Override
    public User getEntityById(UUID id) {
        log.info("[UserRepository] Searching user by id: {}", id);
        return userRepository.findByPublicIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(USER, id.toString()));
    }

    @Override
    public User getEntityByEmail(String email) {
        log.info("[UserRepository] Searching user by email: {}", email);
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException(USER));    }
}

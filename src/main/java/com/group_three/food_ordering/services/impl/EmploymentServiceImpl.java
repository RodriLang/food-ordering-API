package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.mappers.RoleEmploymentMapper;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.services.EmploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmploymentMapper employmentMapper;
    private final RoleEmploymentMapper roleEmploymentMapper;

    @Override
    public EmploymentResponseDto create(EmploymentRequestDto dto) {
        return null;
    }

    @Override
    public List<RoleEmploymentResponseDto> getRoleEmploymentsByUser(UUID userId) {

        List<Employment> employments = employmentRepository.findByUser_IdAndActiveTrue(userId);

        return employments.stream()
                .map(roleEmploymentMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<EmploymentResponseDto> getAll() {
        return List.of();
    }

    @Override
    public List<EmploymentResponseDto> getByUser(UUID userId) {
        return employmentRepository.findByUser_IdAndActiveTrue(userId).stream()
                .map(employmentMapper::toResponseDto).toList();
    }

    @Override
    public EmploymentResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public EmploymentResponseDto update(UUID id, EmploymentRequestDto dto) {
        return null;
    }

    @Override
    public Employment getEntityById(UUID id) {
        return employmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Employment"));
    }
}

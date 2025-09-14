package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.EmployeeCreateDto;
import com.group_three.food_ordering.dto.response.EmployeeResponseDto;
import com.group_three.food_ordering.dto.update.EmployeePatchDto;
import com.group_three.food_ordering.dto.update.EmployeeUpdateDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmployeeMapper;
import com.group_three.food_ordering.models.Employee;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmployeeRepository;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.services.EmployeeService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FoodVenueRepository foodVenueRepository;
    private final UserService userService;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponseDto create(EmployeeCreateDto dto) {
        Employee employee = employeeMapper.toEmployee(dto);

        // Validar que se proporcione un User embebido
        if (dto.getUser() == null) {
            throw new IllegalArgumentException("A full User must be provided to create an Employee.");
        }

        // Validar duplicación de email
        if (userService.getAll().stream().anyMatch(u -> u.getEmail().equals(dto.getUser().getEmail()))) {
            throw new EmailAlreadyUsedException(dto.getUser().getEmail());
        }

        // Asignar automáticamente el rol STAFF
        dto.getUser().setRole(RoleType.ROLE_STAFF);
        User user = userService.createIfPresent(dto.getUser());

        // Asignar usuario
        employee.setUser(user);

        // Asignar local gastronómico
        FoodVenue foodVenue = foodVenueRepository.findById(dto.getFoodVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Food venue", dto.getFoodVenueId().toString()));
        employee.setFoodVenue(foodVenue);

        // Guardar y retornar
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(saved);
    }

    @Override
    public List<EmployeeResponseDto> getAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponseDto)
                .toList();
    }

    @Override
    public EmployeeResponseDto getById(UUID id) {
        Employee employee = employeeRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
        return employeeMapper.toResponseDto(employee);
    }

    @Override
    public void delete(UUID id) {
        Employee employee = employeeRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));

        employee.getUser().setRemovedAt(LocalDateTime.now());
        userService.delete(employee.getUser().getId());
    }

    @Override
    public EmployeeResponseDto update(UUID id, EmployeeUpdateDto dto) {
        Employee employee = this.getEntityById(id);

        // Actualizar campos del employee y del user
        employeeMapper.updateEmployeeFromDto(dto, employee);
        employeeMapper.updateUserFromDto(dto.getUser(), employee.getUser());

        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updated);
    }

    @Override
    public Employee getEntityById(UUID id) {
        return employeeRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
    }

    @Override
    public EmployeeResponseDto replace(UUID id, EmployeeUpdateDto dto) {
        return update(id, dto); // misma lógica que update
    }

    @Override
    public EmployeeResponseDto partialUpdate(UUID id, EmployeePatchDto dto) {
        Employee employee = this.getEntityById(id);

        // Solo actualizar lo que viene no nulo
        if (dto.getPosition() != null) {
            employee.setPosition(dto.getPosition());
        }

        if (dto.getUser() != null) {
            employeeMapper.updateUserFromPatchDto(dto.getUser(), employee.getUser());
        }

        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponseDto(updated);
    }
}

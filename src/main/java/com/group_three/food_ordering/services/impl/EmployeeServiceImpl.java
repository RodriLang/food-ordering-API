package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.EmployeeCreateDto;
import com.group_three.food_ordering.dtos.response.EmployeeResponseDto;
import com.group_three.food_ordering.dtos.update.EmployeeUpdateDto;
import com.group_three.food_ordering.exceptions.EmailAlreadyUsedException;
import com.group_three.food_ordering.exceptions.FoodVenueNotFoundException;
import com.group_three.food_ordering.exceptions.EmployeeNotFoundException;
import com.group_three.food_ordering.exceptions.UserNotFoundException;
import com.group_three.food_ordering.mappers.EmployeeMapper;
import com.group_three.food_ordering.models.Employee;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.UserEntity;
import com.group_three.food_ordering.repositories.IEmployeeRepository;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.services.interfaces.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final IUserRepository userRepository;
    private final IFoodVenueRepository foodVenueRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EmployeeResponseDto create(EmployeeCreateDto dto) {
        UserEntity userEntity = null;

        if (dto.getUserId() != null) {
            userEntity = userRepository.findByIdAndRemovedAtIsNull(dto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));
        }
        else if (dto.getUser() != null) {
            if (userRepository.existsByEmail(dto.getUser().getEmail())) {
                throw new EmailAlreadyUsedException(dto.getUser().getEmail());
            }

            userEntity = employeeMapper.toUser(dto.getUser());
            userEntity.setPassword(passwordEncoder.encode(dto.getUser().getPassword()));
            userRepository.save(userEntity);
        }

        FoodVenue foodVenue = foodVenueRepository.findById(dto.getFoodVenueId())
                .orElseThrow(() -> new FoodVenueNotFoundException(dto.getFoodVenueId()));

        Employee employee = employeeMapper.toEmployee(dto);
        employee.setUserEntity(userEntity);
        employee.setFoodVenue(foodVenue);

        employeeRepository.save(employee);

        return employeeMapper.toResponseDto(employee);
    }

    @Override
    public EmployeeResponseDto update(UUID id, EmployeeUpdateDto dto) {
        Employee employee = employeeRepository.findByIdAndUserEntity_RemovedAtIsNull(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (dto.getUser() != null && employee.getUserEntity() != null) {
            employeeMapper.updateUserFromDto(dto.getUser(), employee.getUserEntity());
        }

        employeeMapper.updateEmployeeFromDto(dto, employee);
        employeeRepository.save(employee);

        return employeeMapper.toResponseDto(employee);
    }

    @Override
    public EmployeeResponseDto getById(UUID id) {
        Employee employee = employeeRepository.findByIdAndUserEntity_RemovedAtIsNull(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return employeeMapper.toResponseDto(employee);
    }

    @Override
    public List<EmployeeResponseDto> getAll() {
        return employeeRepository.findAllByUserEntity_RemovedAtIsNull()
                .stream()
                .map(employeeMapper::toResponseDto)
                .collect(toList());
    }
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.Employee;
import com.group_three.food_ordering.models.Table;
import com.group_three.food_ordering.models.UserEntity;
import com.group_three.food_ordering.repositories.IClientRepository;
import com.group_three.food_ordering.repositories.IEmployeeRepository;
import com.group_three.food_ordering.repositories.ITableRepository;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IEmployeeRepository employeeRepository;
    private final IClientRepository clientRepository;
    private final ITableRepository tableRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

   /* public AuthResponse register(RegisterRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.valueOf(request.getRole()));
        userRepository.save(userEntity);

        String token = jwtService.generateToken(userEntity);

        return new AuthResponse(token);
    }*/

    public AuthResponse loginEmployee(LoginRequest request) {
        Employee employee = employeeRepository.findByUserEntity_Email(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), employee.getUserEntity().getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtUtil.generateToken(employee.getUserEntity().getEmail(), employee.getFoodVenue().getId(), employee.getUserEntity().getRole());

        return new AuthResponse(token);
    }

    public AuthResponse loginClient(LoginRequest request, UUID tableId) {
        Client client = clientRepository.findByUserEntity_Email(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabla no encontrada"));

        if (!passwordEncoder.matches(request.getPassword(), client.getUserEntity().getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtUtil.generateToken(client.getUserEntity().getEmail(), table.getFoodVenue().getId(), client.getUserEntity().getRole());

        return new AuthResponse(token);
    }
}
package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.Employee;
import com.group_three.food_ordering.models.Table;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.IClientRepository;
import com.group_three.food_ordering.repositories.IEmployeeRepository;
import com.group_three.food_ordering.repositories.ITableRepository;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final IEmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

   /* public AuthResponse register(RegisterRequest request) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.valueOf(request.getRole()));
        userRepository.save(userEntity);

        String token = jwtService.generateToken(userEntity);

        return new AuthResponse(token);
    }*/

    /*public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Optional<UUID> foodVenueId = getFoodVenueIdIfStaff(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                foodVenueId.orElse(null),
                user.getRole().name()
        );
        return new AuthResponse(token);
    }

    private Optional<UUID> getFoodVenueIdIfStaff(User user) {
        if (user.getRole() != RoleType.ROLE_STAFF) {
            return Optional.empty();
        }
        return employeeRepository.findByUser_Email(user.getEmail())
                .map(employee -> employee.getFoodVenue().getId());
    }*/

    public AuthResponse login(LoginRequest loginRequest) {

        Optional<Employee> employeeOpt = employeeRepository.findByUser_Email(loginRequest.getEmail());

        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get();

            if (passwordEncoder.matches(loginRequest.getPassword(), emp.getUser().getPassword())) {
                String token = jwtService.generateToken(loginRequest.getEmail(), emp.getFoodVenue().getId(), emp.getUser().getRole().name());

                return new AuthResponse(token);
            }
        }

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(loginRequest.getEmail(), null, user.getRole().name());
                return new AuthResponse(token);
            }
        }

        throw new BadCredentialsException("Usuario o contraseña incorrectos");
    }
}
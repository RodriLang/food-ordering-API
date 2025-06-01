package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.UserEntity;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
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

    public AuthResponse login(LoginRequest request) {
        System.out.println("HOLA");
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        String token = jwtUtil.generateToken(userEntity.getEmail());

        return new AuthResponse(token);
    }
}
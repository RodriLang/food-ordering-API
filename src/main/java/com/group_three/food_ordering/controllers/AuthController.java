package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.security.JwtUtil;
import com.group_three.food_ordering.services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(ApiPaths.AUTH_URI)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

  /*  @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.USER); // o request.getRole()
        userRepository.save(userEntity);

        String token = jwtUtil.generateToken(userEntity.getEmail());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "message", "Registro exitoso",
                "username", userEntity.getEmail()
        ));
    }*/

}
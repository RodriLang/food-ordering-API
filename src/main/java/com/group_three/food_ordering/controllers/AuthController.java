package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.security.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH_URI)
public interface AuthController {


    @Operation(
            summary = "Iniciar sesión",
            description = "Devuelve un token de autenticación con ROLE_CLIENT. " +
                    "En caso de contar con roles especiales, proporciona opciones de selección"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);


    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un usuario con todos sus datos. El correo electrónico debe ser único.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/register")
    ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserCreateDto dto);
}
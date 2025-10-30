package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.utils.constants.ApiPaths;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.utils.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.AUTH_URI)
@Tag(name = "Autenticación", description = "Acceso al login, logout, registro y refresh token")
public interface AuthController {


    @Operation(
            summary = "Iniciar sesión",
            description = "Devuelve un token de autenticación con ROLE_CLIENT. " +
                    "En caso de contar con roles especiales, proporciona opciones de selección"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Solo cliente",
                                            description = "Usuario únicamente con rol de cliente",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbGllbnRAdGVzdC5jb20iLCJyb2xlIjoiUk9MRV9DTElFTlQiLCJpYXQiOjE3NTkwMzU3MDcsImV4cCI6MTc1OTEyMjEwN30.NOLH_ivc4vZr7EHd33hMQ6rpAQt_i1OircL6PQ2E6wHWP7MkR_Y-CPUJDGVbKHl3qBi00rLjqIMrtgTNKOwupA"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Con empleos disponibles",
                                            description = "Usuario con roles en establecimientos",
                                            value = """
                                                    {
                                                      "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsInJvbGUiOiJST0xFX0NMSUVOVCIsImlhdCI6MTc1OTAzNTkwNiwiZXhwIjoxNzU5MTIyMzA2fQ.5kMqfc1kslTTJF7daAcpjsNVoN5bnMU4h5nMj7wA9tr_SwmA1AlJ9dietGHRV7cIldWEPcCdbA89Sbx7nIPZkQ",
                                                      "employments": [
                                                        {
                                                          "id": "0c7e8844-99e8-4bef-9a21-39d9a24cdeb1",
                                                          "role": "ROLE_MANAGER",
                                                          "foodVenueName": "Taco Town"
                                                        },
                                                        {
                                                          "id": "2900ba79-6711-4a51-a5c1-5ec82dd80552",
                                                          "role": "ROLE_ADMIN",
                                                          "foodVenueName": "Burger House"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )


                            })),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (datos incompletos o mal formados)"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario suspendido o inactivo)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request);


    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un usuario con todos sus datos. El correo electrónico debe ser único.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/register")
    ResponseEntity<UserDetailResponseDto> register(
            @Validated(OnCreate.class) @RequestBody UserRequestDto dto);


    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar access token",
            description = "Genera un nuevo access token usando el refresh token válido"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
     ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request);


    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token")
    ResponseEntity<Void> logout(@RequestBody(required = false) RefreshTokenRequest request);

}
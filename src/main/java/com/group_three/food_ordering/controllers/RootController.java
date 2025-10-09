package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.ROOT_ACCESS_URI)
@Tag(name = "Acceso root", description = "Operaciones relacionadas con los usuarios root del sistema")
public interface RootController {

    @Operation(
            summary = "Obtener todos los usuarios root",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/all")
    ResponseEntity<PageResponse<EmploymentResponseDto>> getAllRootUsers(@Parameter(hidden = true) Pageable pageable);

    @Operation(
            summary = "Registrar un nuevo usuario root",
            description = "Crea un usuario con los máximos privilegios.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/register")
    ResponseEntity<EmploymentResponseDto> registerRootUser(
            @Validated(OnCreate.class) @RequestBody EmploymentRequestDto dto);

    @Operation(
            summary = "Seleccionar un FoodVenue",
            description = "Entrar en un Tenant Context para gestionar entidades",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/select-context")
    ResponseEntity<AuthResponse> selectContext(
            @RequestParam UUID foodVenueId);

}

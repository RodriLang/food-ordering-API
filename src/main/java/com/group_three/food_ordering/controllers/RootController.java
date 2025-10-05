package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.ROOT_ACCESS_URI)
public interface RootController {

    @Operation(
            summary = "Obtener todos los usuarios root",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/all")
    ResponseEntity<Page<EmploymentResponseDto>> getAllRootUsers(@Parameter(hidden = true) Pageable pageable);

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
            @Valid @RequestBody EmploymentRequestDto dto);

    @Operation(
            summary = "Registrar un nuevo usuario root",
            description = "Crea un usuario con los máximos privilegios.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/select-context")
    ResponseEntity<EmploymentResponseDto> selectContext(
            @RequestParam UUID foodVenueId);

}

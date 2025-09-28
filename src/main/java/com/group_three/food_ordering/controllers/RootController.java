package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.RootUserRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    ResponseEntity<List<FoodVenuePublicResponseDto>> getAllRootUsers();


    @Operation(
            summary = "Registrar un nuevo usuario root",
            description = "Crea un usuario con los máximos privilegios.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/register")
    ResponseEntity<UserResponseDto> registerRootUser(
            @Valid @RequestBody RootUserRequestDto dto);


    @Operation(
            summary = "Registrar un nuevo usuario root",
            description = "Crea un usuario con los máximos privilegios.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/select-context/{foodVenueId}")
    ResponseEntity<UserResponseDto> selectContext(
            @PathVariable UUID foodVenueId);

}

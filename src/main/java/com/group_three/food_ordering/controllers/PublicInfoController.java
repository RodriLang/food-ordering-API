package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(ApiPaths.PUBLIC_URI)
public interface PublicInfoController {

    @Operation(
            summary = "Obtener todos los lugares de comida",
            description = "Devuelve la lista con datos reducidos de lugares de comida.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de lugares de comida", content = @Content(
                            schema = @Schema(implementation = FoodVenuePublicResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/food-venues")
    ResponseEntity<Page<FoodVenuePublicResponseDto>> getPublicFoodVenues(@Parameter Pageable pageable);


    @PostMapping
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Crea un usuario con todos sus datos. El correo electrónico debe ser único.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserCreateDto dto);

}

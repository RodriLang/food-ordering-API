package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.response.FlatMenuResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.HierarchicalMenuResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping(ApiPaths.PUBLIC_URI)
@Tag(name = "Información pública", description = "Acceso a información pública de los lugares de comida")
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


    @Operation(summary = "Listar todos los productos del Menu ordenados por categoria")
    @ApiResponse(responseCode = "200", description = "Listado de productos por categoria")
    @GetMapping("/food-venues/{foodVenueId}/menu")
    ResponseEntity<FlatMenuResponseDto> getFlatMenu(@PathVariable UUID foodVenueId);

    @Operation(summary = "Listar todos los productos del Menu ordenados por categoria")
    @ApiResponse(responseCode = "200", description = "Listado de productos por categoria")
    @GetMapping("/food-venues/{foodVenueId}/menu/categories")
    ResponseEntity<HierarchicalMenuResponseDto> getHierarchicalMenu(@PathVariable UUID foodVenueId, @RequestParam(required = false) String category);
}

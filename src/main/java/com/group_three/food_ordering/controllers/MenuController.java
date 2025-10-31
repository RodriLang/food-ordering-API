package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.utils.constants.ApiPaths;
import com.group_three.food_ordering.dto.response.MenuResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(ApiPaths.MENU_URI)
@Tag(name = "Menu", description = "Visualización del menu correspondiente a cada lugar de comida")
public interface MenuController {

    @Operation(summary = "Muestra los productos disponibles ordenados por categoría")
    @ApiResponse(responseCode = "200", description = "Listado de productos por categoría")
    @GetMapping()
    ResponseEntity<MenuResponseDto> getHierarchicalMenu(
            @RequestParam(required = false) String category);

}

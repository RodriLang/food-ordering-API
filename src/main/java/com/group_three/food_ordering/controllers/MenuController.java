package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.response.FlatMenuResponseDto;
import com.group_three.food_ordering.dto.response.HierarchicalMenuResponseDto;
import com.group_three.food_ordering.services.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.MENU_URI)
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "Listar todos los productos del Menu ordenados por categoria")
    @ApiResponse(responseCode = "200", description = "Listado de productos por categoria")
    @GetMapping
    public ResponseEntity<FlatMenuResponseDto> getFlatMenu() {
        return ResponseEntity.ok(menuService.getCurrentContextFlatMenu());
    }

    @Operation(summary = "Listar todos los productos del Menu ordenados por categoria")
    @ApiResponse(responseCode = "200", description = "Listado de productos por categoria")
    @GetMapping("/categories")
    public ResponseEntity<HierarchicalMenuResponseDto> getHierarchicalMenu(@RequestParam (required = false) String category) {
        return ResponseEntity.ok(menuService.getCurrentContextHierarchicalMenu(category));
    }
}

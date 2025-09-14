package com.group_three.food_ordering.controllers;


import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.response.MenuResponseDto;
import com.group_three.food_ordering.services.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.MENU_URI)
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "Listar todos los productos del Menu ordenados por categoria")
    @ApiResponse(responseCode = "200", description = "Listado de productos por categoria")
    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getMenu() {
        return ResponseEntity.ok(menuService.getMenu());
    }

}

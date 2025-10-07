package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(ApiPaths.CATEGORY_URI)
@Tag(name = "Categorías", description = "Gestión de las categorías de los productos")
public interface CategoryController {

    @Operation(summary = "Crear una nueva categoría")
    @ApiResponse(responseCode = "200", description = "Categoría creada correctamente")
    @PostMapping
    ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody @Validated(OnCreate.class) CategoryRequestDto categoryRequestDto);


    @Operation(summary = "Actualizar una categoría existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable UUID id,
                                                       @RequestBody @Validated(OnUpdate.class) CategoryRequestDto categoryRequestDto);


    @Operation(summary = "Listar todas las categorías")
    @ApiResponse(responseCode = "200", description = "Listado de categorías")
    @GetMapping
    ResponseEntity<List<CategoryResponseDto>> getAll();


    @Operation(summary = "Obtener una categoría por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable UUID id);


    @Operation(summary = "Eliminar una categoría")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCategory(@PathVariable UUID id);

}

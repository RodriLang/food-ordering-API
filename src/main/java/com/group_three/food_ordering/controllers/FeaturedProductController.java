package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.FeaturedProductRequestDto;
import com.group_three.food_ordering.dto.response.FeaturedProductResponseDto;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.FEAT_PRODUCT_URI)
public interface FeaturedProductController {

    @Operation(
            summary = "Obtener todos los Productos destacados. Activos o inactivos",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/all")
    ResponseEntity<Page<FeaturedProductResponseDto>> getAllFeatProducts(@Parameter(hidden = true) Pageable pageable);

    @Operation(
            summary = "Obtener todos los Productos destacados activos",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/actives")
    ResponseEntity<Page<FeaturedProductResponseDto>> getActivesFeatProducts(@Parameter(hidden = true) Pageable pageable);

    @Operation(
            summary = "Registrar un nuevo Producto destacado",
            description = "Se selecciona un producto para mostrarse como destacado. Solo puede existir uno activo por producto",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/register")
    ResponseEntity<FeaturedProductResponseDto> createFeatProduct(
            @Valid @RequestBody FeaturedProductRequestDto dto);

    @Operation(
            summary = "Obtener un Producto destacado por identificador único",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/id/{id}")
    ResponseEntity<FeaturedProductResponseDto> getById(@Parameter(hidden = true) Pageable pageable, @PathVariable UUID id);

    @Operation(
            summary = "Obtener el Producto destacado activo relacionado a un producto",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/productId/{productId}")
    ResponseEntity<FeaturedProductResponseDto> getActiveByProductId(@PathVariable UUID productId);

    @Operation(
            summary = "Modificar un producto destacado",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @PatchMapping("/productId/{productId}")
    ResponseEntity<FeaturedProductResponseDto> update(
            @PathVariable UUID productId,
            @RequestParam @Validated(OnUpdate.class) FeaturedProductRequestDto dto);

    @Operation(
            summary = "Deshabilitar un producto destacado a partir de un producto",
            description = "Modifica la fecha de fin al momento de la consulta",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/productId/{productId}")
    ResponseEntity<FeaturedProductResponseDto> disable(@Parameter(hidden = true) Pageable pageable, @PathVariable UUID productId);

    @Operation(
            summary = "Eliminar un producto destacado",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/id/{id}")
    ResponseEntity<FeaturedProductResponseDto> delete(@Parameter(hidden = true) Pageable pageable, @PathVariable UUID id);

}

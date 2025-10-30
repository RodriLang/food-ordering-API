package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.utils.constants.ApiPaths;
import com.group_three.food_ordering.dto.request.FeaturedProductRequestDto;
import com.group_three.food_ordering.dto.response.FeaturedProductResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
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

@RequestMapping(ApiPaths.FEAT_PRODUCT_URI)
@Tag(name = "Productos destacados", description = "Gestión de los productos destacados para mostrar en apartados")
public interface FeaturedProductController {


    @Operation(
            summary = "Obtener todos los Productos destacados. Activos o inactivos",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/all")
    ResponseEntity<PageResponse<FeaturedProductResponseDto>> getAllFeatProducts(@Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener todos los Productos destacados activos",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/actives")
    ResponseEntity<PageResponse<FeaturedProductResponseDto>> getActivesFeatProducts(@Parameter(hidden = true) Pageable pageable);


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
            @Validated(OnCreate.class) @RequestBody FeaturedProductRequestDto dto);


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
    @GetMapping("/products/{productName}")
    ResponseEntity<FeaturedProductResponseDto> getActiveByProductId(@PathVariable String productName);


    @Operation(
            summary = "Modificar un producto destacado",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @PatchMapping("/products/{id}")
    ResponseEntity<FeaturedProductResponseDto> update(
            @PathVariable UUID id,
            @RequestParam @Validated(OnUpdate.class) FeaturedProductRequestDto dto);


    @Operation(
            summary = "Habilitar un producto destacado",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404")
            }
    )
    @PatchMapping("/products/{productName}/on")
    ResponseEntity<Void> enable(
            @PathVariable String productName);


    @Operation(
            summary = "Deshabilitar un producto destacado a partir de un producto",
            description = "Modifica la fecha de fin al momento de la consulta",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404")
            }
    )
    @PatchMapping("/products/{productName}/off")
    ResponseEntity<Void> disable(@PathVariable String productName);


    @Operation(
            summary = "Eliminar un producto destacado",
            responses = {
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "404")
            }
    )
    @DeleteMapping("/id/{id}")
    ResponseEntity<FeaturedProductResponseDto> delete(@PathVariable UUID id);

}

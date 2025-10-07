package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.PRODUCT_URI)
@Tag(name = "Productos", description = "Gestión de los productos de los lugares de comida")
public interface ProductController {

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponse(responseCode = "200", description = "Producto creado correctamente")
    @PostMapping
    ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody @Validated(OnCreate.class) ProductRequestDto productRequestDto);

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Listado de productos")
    @GetMapping
    ResponseEntity<Page<ProductResponseDto>> getProducts(Pageable pageable);


    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar productos disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de productos disponibles")
    @GetMapping("/available")
    ResponseEntity<Page<ProductResponseDto>> getProductsAvailable(Pageable pageable);


    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'MANAGER')")
    @GetMapping("/find-by-id/{id}")
    ResponseEntity<ProductResponseDto> getProductById(@PathVariable UUID id);


    @Operation(
            summary = "Obtener productos populares",
            description = "Devuelve una lista paginada con la cantidad indicada productos con mas ventas en el tiempo solicitado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'MANAGER')")
    @GetMapping("/top-selling")
    ResponseEntity<Page<ItemMenuResponseDto>> getTopSellingProducts(
            @RequestParam Integer limit,
            @RequestParam Integer days,
            @Parameter Pageable pageable
    );


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/find-by-name/{productName}")
    ResponseEntity<ItemMenuResponseDto> getProductByNameAndContext(@PathVariable String productName);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Actualizar parcialmente un producto")
    @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente")
    @PatchMapping("/{id}")
    ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody ProductRequestDto productRequestDto);


    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @Operation(summary = "Eliminar un producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    @DeleteMapping("/{id}")
    ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable UUID id);

}

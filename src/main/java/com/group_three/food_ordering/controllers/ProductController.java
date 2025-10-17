package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequestMapping(ApiPaths.PRODUCT_URI)
@Tag(name = "Productos", description = "Gestión de los productos de los lugares de comida")
public interface ProductController {

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponse(responseCode = "200", description = "Producto creado correctamente")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ProductResponseDto> createProduct(
            @RequestPart("product") @Valid ProductRequestDto productRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile image
    );

    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Listado de productos")
    @GetMapping
    ResponseEntity<PageResponse<ProductResponseDto>> getProducts(Pageable pageable);


    @Operation(summary = "Listar productos disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de productos disponibles")
    @GetMapping("/available")
    ResponseEntity<PageResponse<ProductResponseDto>> getProductsAvailable(Pageable pageable);


    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
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
    @GetMapping("/top-selling")
    ResponseEntity<PageResponse<ItemMenuResponseDto>> getTopSellingProducts(
            @RequestParam Integer limit,
            @RequestParam Integer days,
            @Parameter Pageable pageable
    );


    @Operation(
            summary = "Obtener producto por nombre",
            description = "Devuelve un producto usando como identificador su nombre y el contexto actual"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/find-by-name/{productName}")
    ResponseEntity<ItemMenuResponseDto> getProductByNameAndContext(@PathVariable String productName);


    @Operation(summary = "Actualizar parcialmente un producto")
    @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente")
    @PatchMapping("/{id}")
    ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody ProductRequestDto productRequestDto);


    @Operation(summary = "Eliminar un producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    @DeleteMapping("/{id}")
    ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable UUID id);

}

package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping(ApiPaths.PRODUCT_URI)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @Operation(summary = "Crear un nuevo producto")
    @ApiResponse(responseCode = "200", description = "Producto creado correctamente")
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody @Valid ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.create(productRequestDto));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Listado de productos")
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar productos disponibles")
    @ApiResponse(responseCode = "200", description = "Listado de productos disponibles")
    @GetMapping("/available")
    public ResponseEntity<List<ProductResponseDto>> getProductsAvailable() {
        return ResponseEntity.ok(productService.getAllAvailable());
    }

    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })

    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'MANAGER')")
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/find-by-name/{productName}")
    public ResponseEntity<ItemMenuResponseDto> getProductByNameAndContext(@PathVariable String productName) {
        return ResponseEntity.ok(productService.getByNameAndContext(productName));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Actualizar parcialmente un producto")
    @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(productService.update(id, productRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @Operation(summary = "Eliminar un producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

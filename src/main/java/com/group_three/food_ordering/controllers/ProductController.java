package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.ProductCreateDto;
import com.group_three.food_ordering.dtos.response.ProductResponseDto;
import com.group_three.food_ordering.dtos.update.ProductUpdateDto;
import com.group_three.food_ordering.services.interfaces.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.PRODUCT_BASE)
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody @Valid ProductCreateDto productCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.create(productCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts()
    {
        return ResponseEntity.ok(productService.getAll());
    }
    @GetMapping("/available")
    public ResponseEntity<List<ProductResponseDto>> getProductsAvailable()
    {
        return ResponseEntity.ok(productService.getAllAvailable());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> replaceProduct(
            @PathVariable Long id,@Valid @RequestBody ProductCreateDto productCreateDto) {
        return ResponseEntity.ok(productService.replace(id, productCreateDto));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductUpdateDto productUpdateDto)
    {
        return ResponseEntity.ok(productService.update(id, productUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.ProductController;
import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.enums.CloudinaryFolder;
import com.group_three.food_ordering.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    @Override
    public ResponseEntity<ProductResponseDto> createProduct(ProductRequestDto productRequestDto, MultipartFile image, CloudinaryFolder cloudinaryFolder) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productRequestDto, image, cloudinaryFolder));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<ProductResponseDto>> getProducts(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(productService.getAll(pageable)));
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<PageResponse<ProductResponseDto>> getProductsAvailable(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(productService.getAllAvailable(pageable)));
    }

    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<ProductResponseDto> getProductById(UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ROOT', 'ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<PageResponse<ItemMenuResponseDto>> getTopSellingProducts(
            Integer limit,
            Integer days,
            Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.of(productService.getTopSellingProducts(limit, days, pageable)));
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<ItemMenuResponseDto> getProductByNameAndContext(String productName) {
        return ResponseEntity.ok(productService.getByNameAndContext(productName));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<ProductResponseDto> updateProduct(
            UUID id, ProductRequestDto productRequestDto) {
        return ResponseEntity.ok(productService.update(id, productRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    @Override
    public ResponseEntity<ProductResponseDto> deleteProduct(UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

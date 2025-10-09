package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.FeaturedProductController;
import com.group_three.food_ordering.dto.request.FeaturedProductRequestDto;
import com.group_three.food_ordering.dto.response.FeaturedProductResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.services.FeaturedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FeaturedProductControllerImpl implements FeaturedProductController {

    private final FeaturedProductService featuredProductService;

    @Override
    public ResponseEntity<PageResponse<FeaturedProductResponseDto>> getAllFeatProducts(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(featuredProductService.getAll(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<FeaturedProductResponseDto>> getActivesFeatProducts(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(featuredProductService.findActiveFeaturedProducts(pageable)));
    }

    @Override
    public ResponseEntity<FeaturedProductResponseDto> createFeatProduct(FeaturedProductRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(featuredProductService.create(dto));
    }

    @Override
    public ResponseEntity<FeaturedProductResponseDto> getById(Pageable pageable, UUID id) {
        return ResponseEntity.ok(featuredProductService.getById(id));
    }

    @Override
    public ResponseEntity<FeaturedProductResponseDto> getActiveByProductId(UUID productId) {
        return ResponseEntity.ok(featuredProductService.findActiveByProductId(productId));
    }

    @Override
    public ResponseEntity<FeaturedProductResponseDto> update(UUID productId, FeaturedProductRequestDto dto) {
        return ResponseEntity.ok(featuredProductService.update(productId, dto));
    }

    @Override
    public ResponseEntity<Void> disable(Pageable pageable, UUID productId) {
        featuredProductService.disableByProductId(productId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<FeaturedProductResponseDto> delete(Pageable pageable, UUID id) {
        featuredProductService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

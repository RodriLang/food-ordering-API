package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.FeaturedProductRequestDto;
import com.group_three.food_ordering.dto.response.FeaturedProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeaturedProductService {

    FeaturedProductResponseDto create(FeaturedProductRequestDto dto);

    FeaturedProductResponseDto getById(UUID id);

    FeaturedProductResponseDto findActiveByProductId(UUID productId);

    Page<FeaturedProductResponseDto> getAll(Pageable pageable);

    Page<FeaturedProductResponseDto> findActiveFeaturedProducts(Pageable pageable);

    FeaturedProductResponseDto update(UUID id, FeaturedProductRequestDto dto);

    void disableByProductId(UUID productId);

    void deleteById(UUID id);
}

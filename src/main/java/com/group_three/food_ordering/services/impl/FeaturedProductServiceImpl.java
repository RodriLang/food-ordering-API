package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.FeaturedProductRequestDto;
import com.group_three.food_ordering.dto.response.FeaturedProductResponseDto;
import com.group_three.food_ordering.mappers.FeaturedProductMapper;
import com.group_three.food_ordering.models.FeaturedProduct;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.FeaturedProductRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.services.FeaturedProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.FEAT_PRODUCT;
import static com.group_three.food_ordering.utils.EntityName.PRODUCT;

@Service
@RequiredArgsConstructor
public class FeaturedProductServiceImpl implements FeaturedProductService {

    private final FeaturedProductRepository featuredProductRepository;
    private final FeaturedProductMapper featuredProductMapper;
    private final ProductRepository productRepository;

    @Override
    public FeaturedProductResponseDto create(FeaturedProductRequestDto dto) {
        Product product = getProductById(dto.getProductId());
        FeaturedProduct featuredProduct = featuredProductMapper.toEntity(dto);
        featuredProduct.setProduct(product);
        featuredProduct.setPublicId(UUID.randomUUID());
        FeaturedProduct saved = featuredProductRepository.save(featuredProduct);
        return featuredProductMapper.toDto(saved);
    }

    @Override
    public FeaturedProductResponseDto getById(UUID id) {
        FeaturedProduct featuredProduct = getFeaturedProductById(id);
        return featuredProductMapper.toDto(featuredProduct);
    }

    @Override
    public FeaturedProductResponseDto findActiveByProductId(UUID productId) {
        FeaturedProduct featuredProduct = getActiveFeaturedProductByProductId(productId);
        return featuredProductMapper.toDto(featuredProduct);
    }

    @Override
    public Page<FeaturedProductResponseDto> getAll(Pageable pageable) {
        return featuredProductRepository.findActiveFeaturedProducts(pageable)
                .map(featuredProductMapper::toDto);
    }

    @Override
    public Page<FeaturedProductResponseDto> findActiveFeaturedProducts(Pageable pageable) {
        return featuredProductRepository.findActiveFeaturedProducts(pageable)
                .map(featuredProductMapper::toDto);
    }

    @Override
    public FeaturedProductResponseDto update(UUID id, FeaturedProductRequestDto dto) {
        FeaturedProduct featuredProduct = getFeaturedProductById(id);
        featuredProductMapper.updateEntity(featuredProduct, dto);
        FeaturedProduct saved = featuredProductRepository.save(featuredProduct);
        return featuredProductMapper.toDto(saved);
    }

    @Override
    public void disableByProductId(UUID productId) {
        FeaturedProduct featuredProduct = getActiveFeaturedProductByProductId(productId);
        featuredProduct.setFeaturedUntil(LocalDateTime.now());
        featuredProductRepository.save(featuredProduct);
    }

    @Override
    public void deleteById(UUID id) {
        FeaturedProduct featuredProduct = getFeaturedProductById(id);
        featuredProductRepository.delete(featuredProduct);
    }

    private FeaturedProduct getFeaturedProductById(UUID id) {
        return featuredProductRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(FEAT_PRODUCT));
    }

    private FeaturedProduct getActiveFeaturedProductByProductId(UUID productId) {
        return featuredProductRepository.findActiveByProduct_PublicId(productId)
                .orElseThrow(() -> new EntityNotFoundException(FEAT_PRODUCT));
    }

    private Product getProductById(UUID id) {
        return productRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT));
    }
}

package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
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

import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.FEAT_PRODUCT;
import static com.group_three.food_ordering.utils.EntityName.PRODUCT;

@Service
@RequiredArgsConstructor
public class FeaturedProductServiceImpl implements FeaturedProductService {

    private final FeaturedProductRepository featuredProductRepository;
    private final FeaturedProductMapper featuredProductMapper;
    private final ProductRepository productRepository;
    private final TenantContext tenantContext;

    @Override
    public FeaturedProductResponseDto create(FeaturedProductRequestDto dto) {
        Product product = getProductByNameAndContext(dto.getProductName());
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
    public FeaturedProductResponseDto findActiveByProductId(String productName) {
        FeaturedProduct featuredProduct = getActiveFeaturedProductByProductNameAndContext(productName);
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
    public void enableByProduct(String productName) {
        FeaturedProduct featuredProduct = getActiveFeaturedProductByProductNameAndContext(productName);
        featuredProduct.setActive(Boolean.TRUE);
        featuredProductRepository.save(featuredProduct);
    }

    @Override
    public void disableByProduct(String productName) {
        FeaturedProduct featuredProduct = getActiveFeaturedProductByProductNameAndContext(productName);
        featuredProduct.setActive(Boolean.FALSE);
        featuredProductRepository.save(featuredProduct);
    }

    @Override
    public void deleteById(UUID id) {
        FeaturedProduct featuredProduct = getFeaturedProductById(id);
        featuredProduct.setDeleted(Boolean.TRUE);
        featuredProductRepository.save(featuredProduct);
    }

    private FeaturedProduct getFeaturedProductById(UUID id) {
        return featuredProductRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(FEAT_PRODUCT));
    }

    private FeaturedProduct getActiveFeaturedProductByProductNameAndContext(String productName) {
        UUID currentContextId = tenantContext.getCurrentFoodVenueId();
        return featuredProductRepository.findActiveByProduct(productName, currentContextId)
                .orElseThrow(() -> new EntityNotFoundException(FEAT_PRODUCT));
    }

    private Product getProductByNameAndContext(String productName) {
        UUID currentContextId = tenantContext.getCurrentFoodVenueId();
        List<Product> products = productRepository.findByNameAndFoodVenue_PublicId(productName, currentContextId);
        if (products.isEmpty()) {
            throw new EntityNotFoundException(PRODUCT);
        }
        return products.getFirst();
    }
}

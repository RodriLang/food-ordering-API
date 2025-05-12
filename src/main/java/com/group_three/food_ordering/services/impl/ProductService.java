package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.ProductCreateDto;
import com.group_three.food_ordering.dtos.response.ProductResponseDto;
import com.group_three.food_ordering.dtos.update.ProductUpdateDto;
import com.group_three.food_ordering.mappers.ProductMapper;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.IProductRepository;
import com.group_three.food_ordering.services.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final IProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public ProductResponseDto create(ProductCreateDto productCreateDto) {
        Product product = productMapper.toEntity(productCreateDto);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDto update(ProductUpdateDto productUpdateDto) {
        Product product = new Product();
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        return productMapper.toDTO(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponseDto> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .toList();
    }

}

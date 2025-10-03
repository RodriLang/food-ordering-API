package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.models.Product;

import java.util.List;

public interface ProductService {

    ProductResponseDto create(ProductRequestDto productRequestDto);

    ProductResponseDto update(Long id, ProductRequestDto productRequestDto);

    ProductResponseDto getById(Long id);

    Product getEntityById(Long id);

    ProductResponseDto getByNameAndContext(String name);

    void delete(Long id);

    List<ProductResponseDto> getAll();

    List<ProductResponseDto> getAllAvailable();

    void validateStock(Product product, Integer quantity) throws InsufficientStockException;

    void incrementStockProduct(Product product, Integer quantity);

    void decrementStockProduct(Product product, Integer quantity);

}

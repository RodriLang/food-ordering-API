package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.OrderDetailRequestDto;
import com.group_three.food_ordering.dto.response.OrderDetailResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.OrderDetailRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.services.OrderDetailService;
import com.group_three.food_ordering.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final TenantContext tenantContext;

    private static final String ORDER_DETAIL_ENTITY_NAME = "Order Detail";
    private static final String PRODUCT_ENTITY_NAME = "Product";

    @Transactional
    @Override
    public OrderDetailResponseDto create(OrderDetailRequestDto orderDetailRequestDto) {
        return orderDetailMapper.toDTO(this.createInternal(orderDetailRequestDto));
    }

    public OrderDetail createInternal(OrderDetailRequestDto orderDetailRequestDto) {
        Product product = productService.getEntityByNameAndContext(orderDetailRequestDto.getProductName());

        updateProductStock(product, -1);
        OrderDetail orderDetail = orderDetailMapper.toEntity(orderDetailRequestDto);
        orderDetail.setProduct(product);

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetailResponseDto getOrderDetailById(Long orderDetailId) {
        OrderDetail orderDetail = getOrderDetailEntityById(orderDetailId);
        return orderDetailMapper.toDTO(orderDetail);
    }

    @Override
    @Transactional
    public void updateQuantity(Long id, Integer newQuantity) {
        OrderDetail detail = getOrderDetailEntityById(id);

        int currentQuantity = detail.getQuantity();
        int diff = newQuantity - currentQuantity;

        if (diff > 0) {
            productService.validateStock(detail.getProduct(), diff);
            updateProductStock(detail.getProduct(), -diff);
        } else if (diff < 0) {
            updateProductStock(detail.getProduct(), -diff); // -diff es positivo acá
        }

        detail.setQuantity(newQuantity);
        orderDetailRepository.save(detail);
    }

    @Override
    @Transactional
    public OrderDetailResponseDto updateSpecialInstructions(Long id, String instructions) {
        OrderDetail detail = getOrderDetailEntityById(id);

        detail.setSpecialInstructions(instructions);
        OrderDetail saved = orderDetailRepository.save(detail);
        return orderDetailMapper.toDTO(saved);
    }

    private void updateProductStock(Product product, Integer difference) {
        int newQuantity = product.getStock() + difference;
        if (newQuantity < 0) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }
        product.setStock(newQuantity);
        productRepository.save(product);
    }

    private OrderDetail getOrderDetailEntityById(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ORDER_DETAIL_ENTITY_NAME, id.toString()));
    }
}

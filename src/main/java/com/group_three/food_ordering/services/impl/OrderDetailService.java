package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.exceptions.ProductNotFoundException;
import com.group_three.food_ordering.exceptions.OrderDetailNotFoundException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.IOrderDetailRepository;
import com.group_three.food_ordering.repositories.IProductRepository;
import com.group_three.food_ordering.services.interfaces.IOrderDetailService;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import com.group_three.food_ordering.services.interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {

    private final IOrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final IOrderService orderService;
    private final IProductService productService;
    private final IProductRepository productRepository;


    @Transactional
    @Override
    public OrderDetailResponseDto create(UUID orderId, OrderDetailRequestDto orderDetailRequestDto) {
        Order existingOrder = orderService.getEntityById(orderId);

        Product product = productRepository.findById(orderDetailRequestDto.getProductId())
                .orElseThrow(ProductNotFoundException::new);

        this.updateProductStock(product, orderDetailRequestDto.getQuantity());
        
        OrderDetail orderDetail = orderDetailMapper.toEntity(orderDetailRequestDto);
        orderDetail.setProduct(product);

        orderService.addOrderDetailToOrder(existingOrder.getId(), orderDetail);

        OrderDetail saved = orderDetailRepository.save(orderDetail);

        return orderDetailMapper.toDTO(saved);
    }


    @Override
    public List<OrderDetailResponseDto> getAll() {
        return orderDetailRepository.findAllOrderDetailsAndDeletedFalse().
                stream()
                .map(orderDetailMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDetailResponseDto> getOrderDetailsByOrderId(UUID orderId) {
        return orderDetailRepository.findAllByOrder_IdAndDeletedFalse(orderId).stream()
                .map(orderDetailMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public OrderDetailResponseDto getOrderDetailById(Long orderDetailId) {
        return orderDetailMapper.toDTO(orderDetailRepository.findById(orderDetailId)
                .orElseThrow(OrderDetailNotFoundException::new));
    }

    @Override
    public void softDelete(Long orderDetailId) {

        OrderDetail orderDetail = this.getOderDetailById(orderDetailId);
        updateProductStock(orderDetail.getProduct(), orderDetail.getQuantity());

        //Al se borrado lógico se remueve el OrderDetail de Order
        orderService.removeOrderDetailFromOrder(orderDetail.getOrder().getId(), orderDetail);
        orderDetailRepository.save(orderDetail);

    }


    @Override
    @Transactional
    public OrderDetailResponseDto updateQuantity(Long id, Integer newQuantity) {
        OrderDetail detail = orderDetailRepository.findById(id)
                .orElseThrow(OrderDetailNotFoundException::new);

        int currentQuantity = detail.getQuantity();
        int diff = newQuantity - currentQuantity;

        if (diff > 0) {
           // productService.validateStock(detail.getProduct(), diff);
           // productService.decrementStock(detail.getProduct(), diff);
        } else if (diff < 0) {
           // productService.incrementStock(detail.getProduct(), -diff);
        }

        detail.setQuantity(newQuantity);
        orderDetailRepository.save(detail);
        return orderDetailMapper.toDTO(detail);
    }


    @Override
    @Transactional
    public OrderDetailResponseDto updateSpecialInstructions(Long id, String instructions) {
        OrderDetail detail = orderDetailRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(OrderDetailNotFoundException::new);

        detail.setSpecialInstructions(instructions);
        orderDetailRepository.save(detail);
        return orderDetailMapper.toDTO(detail);
    }

    private void updateProductStock(Product product, Integer difference) {

        int newQuantity = product.getStock() + difference;

        if (newQuantity < 0) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }

        // Sumar o descontar stock
        product.setStock(newQuantity);
        productRepository.save(product); // guardar el nuevo stock
    }

    private OrderDetail getOderDetailById(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(OrderDetailNotFoundException::new);
    }


}

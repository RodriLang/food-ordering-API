package com.group_three.food_ordering.services.impl;

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

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final ProductService productService;
    private final ProductRepository productRepository;

    private static final String CLASS_NAME = "Order Detail";

    /**
     * Crea un nuevo detalle para una orden.
     * Actualiza stock del producto correspondiente.
     */
    @Transactional
    @Override
    public OrderDetailResponseDto create(OrderDetailRequestDto orderDetailRequestDto) {

        return orderDetailMapper.toDTO(this.createInternal(orderDetailRequestDto));
    }

    public OrderDetail createInternal(OrderDetailRequestDto orderDetailRequestDto) {

        Product product = productRepository.findById(orderDetailRequestDto.getProductId())
                .orElseThrow(()-> new EntityNotFoundException("Product not found"));

        updateProductStock(product, -1);

        OrderDetail orderDetail = orderDetailMapper.toEntity(orderDetailRequestDto);
        orderDetail.setProduct(product);

        return orderDetailRepository.save(orderDetail);
    }



    @Override
    public List<OrderDetailResponseDto> getAll() {
        return orderDetailRepository.findAllByDeletedFalse()
                .stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    @Override
    public OrderDetailResponseDto getOrderDetailById(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByIdAndDeletedFalse(orderDetailId)
                .orElseThrow(()-> new EntityNotFoundException(CLASS_NAME, orderDetailId.toString()));
        return orderDetailMapper.toDTO(orderDetail);
    }

    /**
     * Borrado lógico (soft delete) de un detalle de orden.
     * Incrementa el stock del producto correspondiente y remueve el detalle de la orden.
     */
    @Override
    @Transactional
    public void softDelete(Long orderDetailId) {
        OrderDetail orderDetail = getOrderDetailEntityById(orderDetailId);

        // Incrementar stock según cantidad del detalle eliminado
        updateProductStock(orderDetail.getProduct(), orderDetail.getQuantity());

        // Marcar como eliminado (borrado lógico)
        orderDetail.setDeleted(true);

        orderDetailRepository.save(orderDetail);

    }

    /**
     * Actualiza la cantidad de un detalle de orden, controlando el stock disponible.
     */
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

    /**
     * Actualiza las instrucciones especiales para un detalle de orden.
     */
    @Override
    @Transactional
    public OrderDetailResponseDto updateSpecialInstructions(Long id, String instructions) {
        OrderDetail detail = orderDetailRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException(CLASS_NAME, id.toString()));

        detail.setSpecialInstructions(instructions);
        OrderDetail saved = orderDetailRepository.save(detail);
        return orderDetailMapper.toDTO(saved);
    }

    /**
     * Método privado para actualizar stock de productos.
     * @param product producto a modificar
     * @param difference diferencia positiva o negativa en stock
     */
    private void updateProductStock(Product product, Integer difference) {
        int newQuantity = product.getStock() + difference;
        if (newQuantity < 0) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }
        product.setStock(newQuantity);
        productRepository.save(product);
    }

    /**
     * Método privado para obtener la entidad OrderDetail verificando existencia y no eliminado.
     */
    private OrderDetail getOrderDetailEntityById(Long id) {
        return orderDetailRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(()-> new EntityNotFoundException(CLASS_NAME, id.toString()));
    }
}

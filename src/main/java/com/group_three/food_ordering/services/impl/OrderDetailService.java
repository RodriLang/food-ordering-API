package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.exceptions.OrderDetailNotFoundException;
import com.group_three.food_ordering.exceptions.ProductNotFoundException;
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

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {

    private final IOrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final IOrderService orderService;
    private final IProductService productService;
    private final IProductRepository productRepository;

    /**
     * Crea un nuevo detalle para una orden.
     * Actualiza stock del producto correspondiente.
     */
    @Transactional
    @Override
    public OrderDetailResponseDto create(UUID orderId, OrderDetailRequestDto orderDetailRequestDto) {
        Order existingOrder = orderService.getEntityById(orderId);

        Product product = productRepository.findById(orderDetailRequestDto.getProductId())
                .orElseThrow(ProductNotFoundException::new);

        updateProductStock(product, -1);

        OrderDetail orderDetail = orderDetailMapper.toEntity(orderDetailRequestDto);
        orderDetail.setProduct(product);
        orderDetail.setOrder(existingOrder);

        OrderDetail saved = orderDetailRepository.save(orderDetail);

        // Asociar el detalle a la orden (según tu lógica en orderService)
        orderService.addOrderDetailToOrder(existingOrder.getId(), orderDetail);

        return orderDetailMapper.toDTO(saved);
    }

    @Override
    public List<OrderDetailResponseDto> getAll() {
        return orderDetailRepository.findAllByDeletedFalse()
                .stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderDetailResponseDto> getOrderDetailsByOrderId(UUID orderId) {
        return orderDetailRepository.findAllByOrder_IdAndDeletedFalse(orderId)
                .stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    @Override
    public OrderDetailResponseDto getOrderDetailById(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByIdAndDeletedFalse(orderDetailId)
                .orElseThrow(OrderDetailNotFoundException::new);
        return orderDetailMapper.toDTO(orderDetail);
    }

    /**
     * Borrado lógico (soft delete) de un detalle de orden.
     * Incrementa el stock del producto correspondiente y remueve el detalle de la orden.
     */
    @Override
    @Transactional
    public void softDelete(UUID orderId, Long orderDetailId) {
        OrderDetail orderDetail = getOrderDetailEntityById(orderDetailId);

        // Verificar que el detalle realmente pertenezca a la orden
        if (!orderDetail.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("El detalle no pertenece a la orden indicada");
        }

        // Incrementar stock según cantidad del detalle eliminado
        updateProductStock(orderDetail.getProduct(), orderDetail.getQuantity());

        // Marcar como eliminado (borrado lógico)
        orderDetail.setDeleted(true);

        // Remover detalle de la orden
        orderService.removeOrderDetailFromOrder(orderDetail.getOrder().getId(), orderDetail);

        orderDetailRepository.save(orderDetail);

    }

    /**
     * Actualiza la cantidad de un detalle de orden, controlando el stock disponible.
     */
    @Override
    @Transactional
    public OrderDetailResponseDto updateQuantity(Long id, Integer newQuantity) {
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
        OrderDetail saved = orderDetailRepository.save(detail);
        return orderDetailMapper.toDTO(saved);
    }

    /**
     * Actualiza las instrucciones especiales para un detalle de orden.
     */
    @Override
    @Transactional
    public OrderDetailResponseDto updateSpecialInstructions(Long id, String instructions) {
        OrderDetail detail = orderDetailRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(OrderDetailNotFoundException::new);

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
                .orElseThrow(OrderDetailNotFoundException::new);
    }
}

package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.LogicalAccessDeniedException;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.mappers.OrderMapper;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.utils.OrderServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final TenantContext tenantContext;
    private final AuthService authService;
    private final TableSessionRepository tableSessionRepository;
    private final OrderServiceHelper orderServiceHelper;

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {

        Client client = authService.getCurrentClient();

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderNumber(orderServiceHelper.generateOrderNumber());
        order.setClient(client);

        FoodVenue currentFoodVenue = tenantContext.getCurrentFoodVenue();

        /// method added for setting Table Session ID in the order
        TableSession tableSession = authService.getCurrentTableSession();
        order.setTableSession(tableSession);


        order.setFoodVenue(currentFoodVenue);
        List<OrderDetail> orderDetails = orderRequestDto.getOrderDetails()
                .stream()
                .map(dto -> {
                    OrderDetail detail = orderDetailMapper.toEntity(dto);
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product", dto.getProductId().toString()));
                    detail.setProduct(product);
                    detail.setQuantity(1);
                    detail.setPrice(product.getPrice());
                    return detail;
                })
                .toList();

        order.setOrderDetails(orderDetails);
        orderServiceHelper.updateTotalPrice(order);
        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override

    public List<OrderResponseDto> getOrdersByFilters(
            LocalDate from, LocalDate to, OrderStatus status) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        return fetchOrders(fromDateTime, toDateTime, status);
    }

    @Override
    public List<OrderResponseDto> getOrdersForToday(OrderStatus status) {
        LocalDateTime opening = LocalDate.now().atStartOfDay();
        LocalDateTime closing = opening.plusDays(1);

        return fetchOrders(opening, closing, status);
    }

    @Override
    public List<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId) {
        return orderRepository.findOrderByTableSession_Id(tableSessionId)
                .stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId, OrderStatus status) {

        Client currentClient = authService.getCurrentClient();


        TableSession session = tableSessionRepository.findById(tableSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Table session not found"));

        if (currentClient.getUser().getRole().equals(RoleType.ROLE_CLIENT)
                && !session.getParticipants().contains(currentClient)) {

            throw new LogicalAccessDeniedException("You do not have access to this table session");
        }

        //filtra por estado
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findOrderByTableSession_IdAndStatus(tableSessionId, status);
        } else {
            orders = orderRepository.findOrderByTableSession_Id(tableSessionId);
        }

        return orders.stream().map(orderMapper::toDTO).toList();

    }

    public List<OrderResponseDto> getOrdersByClient(UUID clientId, OrderStatus status) {

        Client currentClient = authService.getCurrentClient();

        if (currentClient.getUser().getRole().equals(RoleType.ROLE_CLIENT)
                && !currentClient.getId().equals(clientId)) {

            throw new LogicalAccessDeniedException("You do not have access to this table session");
        }

        // Se filtra por estado
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findOrdersByClient_IdAndStatus(clientId, status);
        } else {
            orders = orderRepository.findOrdersByClient_Id(clientId);
        }

        return orders.stream().map(orderMapper::toDTO).toList();
    }


    // permite recibir parámetros opcionalmente
    // omitiendo el filtro que no fue especificado en la consulta
    private List<OrderResponseDto> fetchOrders(
            LocalDateTime from, LocalDateTime to, OrderStatus status) {
        UUID venueId = tenantContext.getCurrentFoodVenueId();
        List<Order> orders;

        if (from != null && to != null) {
            if (status != null) {
                orders = orderRepository.findByFoodVenue_IdAndCreationDateBetweenAndStatus(venueId, from, to, status);
            } else {
                orders = orderRepository.findByFoodVenue_IdAndCreationDateBetween(venueId, from, to);
            }
        } else if (status != null) {
            orders = orderRepository.findByFoodVenue_IdAndStatus(venueId, status);
        } else {
            orders = orderRepository.findByFoodVenue_Id(venueId);
        }

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }


    @Override
    public OrderResponseDto getById(UUID id) {

        return orderMapper.toDTO(this.getEntityById(id));
    }

    @Override
    public OrderResponseDto updateSpecialRequirements(UUID orderId, String specialRequirements) {
        Order existingOrder = this.getEntityById(orderId);

        existingOrder.setSpecialRequirements(specialRequirements);

        orderRepository.save(existingOrder);

        return new OrderResponseDto();
    }


    @Override
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = this.getEntityById(id);

        existingOrder.setStatus(orderStatus);
        orderRepository.save(existingOrder);

        return orderMapper.toDTO(existingOrder);
    }

    @Override
    public OrderResponseDto getOrderByDateAndOrderNumber(
            LocalDate date, Integer orderNumber) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return orderMapper.toDTO(orderRepository
                .findByFoodVenue_IdAndOrderNumberAndCreationDateBetween(
                        tenantContext.getCurrentFoodVenue().getId(), orderNumber, start, end)
                .orElseThrow(() -> new EntityNotFoundException("Order not found")));
    }


    @Override
    public void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().remove(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);

        this.orderRepository.save(existingOrder);
    }

    @Override
    public void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().add(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);

        this.orderRepository.save(existingOrder);
    }

    @Override
    public Order getEntityById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order", id.toString()));
    }
}

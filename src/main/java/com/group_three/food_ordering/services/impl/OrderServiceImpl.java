package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.OrderRequestDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final TenantContext tenantContext;
    private final AuthService authService;
    private final TableSessionRepository tableSessionRepository;
    private final OrderServiceHelper orderServiceHelper;

    private static final String ORDER_ENTITY_NAME = "Order";
    private static final String PRODUCT_ENTITY_NAME = "Product";
    private static final String TABLE_SESSION_ENTITY_NAME = "TableSession";

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        log.debug("[OrderService] Create Order Request");
        FoodVenue currentFoodVenue = tenantContext.determineCurrentFoodVenue();

        Participant participant = authService.determineCurrentParticipant();

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderNumber(orderServiceHelper.generateOrderNumber());
        order.setParticipant(participant);
        order.setStatus(OrderStatus.PENDING);
        order.setPublicId(UUID.randomUUID());
        TableSession tableSession = authService.determineCurrentTableSession();
        order.setTableSession(tableSession);

        // Revisar porque no permite cantidad de productos mayor a 1 como regla de negocio pero se puede evaluar
        order.setFoodVenue(currentFoodVenue);
        List<OrderDetail> orderDetails = orderRequestDto.getOrderDetails()
                .stream()
                .map(dto -> {
                    OrderDetail detail = orderDetailMapper.toEntity(dto);
                    Product product = productRepository.findByPublicId(dto.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException(PRODUCT_ENTITY_NAME, dto.getProductId().toString()));
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

    public Page<OrderResponseDto> getOrdersByFilters(
            LocalDate from, LocalDate to, OrderStatus status, Pageable pageable) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        return fetchOrders(fromDateTime, toDateTime, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersForToday(OrderStatus status, Pageable pageable) {
        LocalDateTime opening = LocalDate.now().atStartOfDay();
        LocalDateTime closing = opening.plusDays(1);

        return fetchOrders(opening, closing, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId, OrderStatus status, Pageable pageable) {

        Participant currentParticipant = authService.determineCurrentParticipant();

        TableSession session = tableSessionRepository.findByPublicId(tableSessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION_ENTITY_NAME, tableSessionId.toString()));

        RoleType role = authService.getCurrentParticipantRole();

        if (role.equals(RoleType.ROLE_CLIENT)
                && !session.getParticipants().contains(currentParticipant)) {

            throw new LogicalAccessDeniedException("You do not have access to this table session");
        }
        //filtra por estado
        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findOrderByTableSession_PublicIdAndStatus(tableSessionId, status, pageable);
        } else {
            orders = orderRepository.findOrderByTableSession_PublicId(tableSessionId, pageable);
        }
        return orders.map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClient(OrderStatus status, Pageable pageable) {
        User authenticatedClient = authService.determineAuthUser();
        return getOrdersByUser(authenticatedClient.getPublicId(), status, pageable);
    }

    private Page<OrderResponseDto> getOrdersByUser(UUID userId, OrderStatus status, Pageable pageable) {
        // Se filtra por estado
        Page<Order> orders;
        if (status != null) {
            orders = orderRepository.findOrdersByParticipant_PublicIdAndStatus(userId, status, pageable);
        } else {
            orders = orderRepository.findOrdersByParticipant_PublicId(userId, pageable);
        }

        return orders.map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClientAndStatus(OrderStatus status, Pageable pageable) {
        log.debug("[OrderService] Get orders by authenticated user");
        UUID currentClientId = authService.determineCurrentParticipant().getPublicId();
        return getOrdersByUser(currentClientId, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(
            OrderStatus status, Pageable pageable) {

        UUID currentClientId = authService.determineCurrentParticipant().getPublicId();
        UUID currentTableSessionId = authService.determineCurrentTableSession().getPublicId();

        return getOrdersByClientAndTableSessionAndStatus(currentClientId, currentTableSessionId, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByClientAndTableSessionAndStatus(
            UUID clientId, UUID tableSessionId, OrderStatus status, Pageable pageable) {

        UUID currentClientId = authService.determineCurrentParticipant().getPublicId();
        UUID currentTableSessionId = authService.determineCurrentTableSession().getPublicId();

        return orderRepository.findOrdersByParticipant_PublicIdAndTableSession_PublicIdAndStatus(
                currentClientId, currentTableSessionId, status, pageable).map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByCurrentParticipant(Pageable pageable) {
        log.debug("[OrderService] Get orders by current participant");
        UUID currentClientId = authService.determineCurrentParticipant().getPublicId();
        log.debug("[OrderService] Current client ID={}", currentClientId);
        return orderRepository.findOrdersByParticipant_PublicId(currentClientId, pageable).map(orderMapper::toDTO);
    }

    // permite recibir parámetros opcionalmente
    // omitiendo el filtro que no fue especificado en la consulta
    private Page<OrderResponseDto> fetchOrders(
            LocalDateTime from,
            LocalDateTime to,
            OrderStatus status,
            Pageable pageable
    ) {
        UUID venueId = tenantContext.determineCurrentFoodVenue().getPublicId();
        Page<Order> orders;

        if (from != null && to != null) {
            if (status != null) {
                orders = orderRepository.findByFoodVenue_PublicIdAndOrderDateBetweenAndStatus(venueId, from, to, status, pageable);
            } else {
                orders = orderRepository.findByFoodVenue_PublicIdAndOrderDateBetween(venueId, from, to, pageable);
            }
        } else if (status != null) {
            orders = orderRepository.findByFoodVenue_PublicIdAndStatus(venueId, status, pageable);
        } else {
            orders = orderRepository.findByFoodVenue_PublicId(venueId, pageable);
        }
        return orders.map(orderMapper::toDTO);
    }

    @Override
    public OrderResponseDto getByIdAndTenantContext(UUID id) {
        return orderMapper.toDTO(this.getEntityByIdAndTenantContext(id));
    }

    @Override
    public OrderResponseDto updateSpecialRequirements(UUID orderId, String specialRequirements) {
        Order existingOrder = this.getEntityByIdAndTenantContext(orderId);
        existingOrder.setSpecialRequirements(specialRequirements);
        orderRepository.save(existingOrder);

        return new OrderResponseDto();
    }

    @Override
    public void delete(UUID id) {
        orderRepository.deleteByPublicId(id);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = this.getEntityByIdAndTenantContext(id);
        existingOrder.setStatus(orderStatus);
        orderRepository.save(existingOrder);

        return orderMapper.toDTO(existingOrder);
    }

    @Override
    public OrderResponseDto getOrderByDateAndOrderNumber(
            LocalDate date, Integer orderNumber) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        Order foundOrder = orderRepository
                .findByFoodVenue_PublicIdAndOrderNumberAndOrderDateBetween(
                        tenantContext.getCurrentFoodVenue().getPublicId(), orderNumber, start, end)
                .orElseThrow(() -> new EntityNotFoundException(ORDER_ENTITY_NAME));

        return orderMapper.toDTO(foundOrder);
    }

    @Override
    public void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityByIdAndTenantContext(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().remove(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);
        this.setDefaultValues(orderDetail);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityByIdAndTenantContext(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().add(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);
        this.setDefaultValues(orderDetail);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public Order getEntityByIdAndTenantContext(UUID id) {
        return orderRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ORDER_ENTITY_NAME, id.toString()));
    }

    private void setDefaultValues(OrderDetail orderDetail) {
        if (orderDetail.getQuantity() == null) orderDetail.setQuantity(1);
        if (orderDetail.getPrice() == null) {
            orderDetail.setPrice(orderDetail.getProduct().getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
        }
    }
}
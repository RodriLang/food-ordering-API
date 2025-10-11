package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.RequestContext;
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
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.ProductService;
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

import static com.group_three.food_ordering.utils.EntityName.ORDER;
import static com.group_three.food_ordering.utils.EntityName.TABLE_SESSION;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final RequestContext requestContext;
    private final TableSessionRepository tableSessionRepository;
    private final OrderServiceHelper orderServiceHelper;

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        log.debug("[OrderService] Create Order Request");
        log.debug("[OrderService] Getting current food Venue.");
        FoodVenue currentFoodVenue = requestContext.requireFoodVenue();
        log.debug("[OrderService] Getting current table session");
        TableSession tableSession = requestContext.requireTableSession();
        log.debug("[OrderService] Getting current participant");
        Participant participant = requestContext.requireParticipant();

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderNumber(orderServiceHelper.generateOrderNumber());
        order.setParticipant(participant);
        order.setStatus(OrderStatus.PENDING);
        order.setPublicId(UUID.randomUUID());
        order.setTableSession(tableSession);
        order.setOrderDate(LocalDateTime.now());

        // Revisar porque no permite cantidad de productos mayor a 1 como regla de negocio pero se puede evaluar
        order.setFoodVenue(currentFoodVenue);
        List<OrderDetail> orderDetails = orderRequestDto.getOrderDetails()
                .stream()
                .map(dto -> {
                    Product product = productService.getEntityByNameAndContext(dto.getProductName());
                    OrderDetail detail = orderDetailMapper.toEntity(dto);
                    detail.setProduct(product);
                    detail.setQuantity(1);
                    detail.setPrice(product.getPrice());
                    return detail;
                })
                .toList();

        order.setOrderDetails(orderDetails);
        orderServiceHelper.updateTotalPrice(order);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override

    public Page<OrderResponseDto> getOrdersByFilters(
            LocalDate from, LocalDate to, OrderStatus status, Pageable pageable) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        return fetchOrders(fromDateTime, toDateTime, status, pageable).map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesByFilters(LocalDate from, LocalDate to, OrderStatus status) {
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.atTime(LocalTime.MAX) : null;

        return fetchOrders(fromDateTime, toDateTime, status, Pageable.unpaged()).toList();
    }


    @Override
    public Page<OrderResponseDto> getOrdersForToday(OrderStatus status, Pageable pageable) {
        LocalDateTime opening = LocalDate.now().atStartOfDay();
        LocalDateTime closing = opening.plusDays(1);

        return fetchOrders(opening, closing, status, pageable).map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesForToday(OrderStatus orderStatus) {
        LocalDateTime opening = LocalDate.now().atStartOfDay();
        LocalDateTime closing = opening.plusDays(1);
        return fetchOrders(opening, closing, null, Pageable.unpaged()).toList();
    }

    @Override
    public Page<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId, OrderStatus status, Pageable pageable) {

        Participant currentParticipant = requestContext.requireParticipant();

        TableSession session = tableSessionRepository.findByPublicId(tableSessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, tableSessionId.toString()));

        RoleType role = requestContext.getRole();

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
        return orders.map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesByTableSessionAndStatus(UUID tableSessionId, OrderStatus orderStatus) {
        TableSession session = tableSessionRepository.findByPublicId(tableSessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, tableSessionId.toString()));

        List<Order> orders;
        if (orderStatus != null) {
            orders = orderRepository.findOrderByTableSession_PublicIdAndStatus(session.getPublicId(), orderStatus, Pageable.unpaged()).toList();
        } else {
            orders = orderRepository.findOrderByTableSession_PublicId(session.getPublicId(), Pageable.unpaged()).toList();
        }
        return orders;
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClient(OrderStatus status, Pageable pageable) {
        User authenticatedClient = requestContext.requireUser();
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

        return orders.map(orderMapper::toDto);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClientAndStatus(OrderStatus status, Pageable pageable) {
        log.debug("[OrderService] Get orders by authenticated user");
        UUID currentClientId = requestContext.requireParticipant().getPublicId();
        return getOrdersByUser(currentClientId, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(
            OrderStatus status, Pageable pageable) {

        UUID currentClientId = requestContext.requireParticipant().getPublicId();
        UUID currentTableSessionId = requestContext.requireTableSession().getPublicId();

        return getOrdersByClientAndTableSessionAndStatus(currentClientId, currentTableSessionId, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByClientAndTableSessionAndStatus(
            UUID clientId, UUID tableSessionId, OrderStatus status, Pageable pageable) {

        UUID currentClientId = requestContext.requireParticipant().getPublicId();
        UUID currentTableSessionId = requestContext.requireTableSession().getPublicId();

        return orderRepository.findOrdersByParticipant_PublicIdAndTableSession_PublicIdAndStatus(
                currentClientId, currentTableSessionId, status, pageable).map(orderMapper::toDto);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByCurrentParticipant(Pageable pageable) {
        log.debug("[OrderService] Get orders by current participant");
        UUID currentClientId = requestContext.requireParticipant().getPublicId();
        log.debug("[OrderService] Current client ID={}", currentClientId);
        return orderRepository.findOrdersByParticipant_PublicId(currentClientId, pageable).map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesByCurrentParticipant() {
        log.debug("[OrderService] Get order entities by current participant");
        UUID currentClientId = requestContext.requireParticipant().getPublicId();
        return orderRepository.findOrdersByParticipant_PublicId(currentClientId, Pageable.unpaged()).toList();
    }

    // permite recibir parámetros opcionalmente
// omitiendo el filtro que no fue especificado en la consulta
    private Page<Order> fetchOrders(
            LocalDateTime from,
            LocalDateTime to,
            OrderStatus status,
            Pageable pageable
    ) {
        UUID venueId = requestContext.requireFoodVenue().getPublicId();
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
        return orders;
    }

    @Override
    public OrderResponseDto getByIdAndTenantContext(UUID id) {
        return orderMapper.toDto(this.getEntityById(id));
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
        Order order = this.getEntityById(id);
        order.setDeleted(Boolean.TRUE);
        orderRepository.save(order);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = this.getEntityById(id);
        Participant participant = requestContext.requireParticipant();
        UUID currentContext = requestContext.requireFoodVenue().getPublicId();

        if (!existingOrder.getFoodVenue().getPublicId().equals(currentContext)) {
            throw new EntityNotFoundException(ORDER);
        }

        if ((participant.getRole().equals(RoleType.ROLE_CLIENT) || participant.getRole().equals(RoleType.ROLE_GUEST)) &&
                !existingOrder.getParticipant().getPublicId().equals(participant.getPublicId())) {
            throw new EntityNotFoundException(ORDER);
        }

        existingOrder.setStatus(orderStatus);
        orderRepository.save(existingOrder);

        return orderMapper.toDto(existingOrder);
    }

    @Override
    public OrderResponseDto getOrderByDateAndOrderNumber(
            LocalDate date, Integer orderNumber) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        UUID currentVenueId = requestContext.requireFoodVenue().getPublicId();
        Order foundOrder = orderRepository
                .findByFoodVenue_PublicIdAndOrderNumberAndOrderDateBetween(currentVenueId, orderNumber, start, end)
                .orElseThrow(() -> new EntityNotFoundException(ORDER));

        return orderMapper.toDto(foundOrder);
    }

    @Override
    public void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().remove(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);
        this.setDefaultValues(orderDetail);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().add(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);
        this.setDefaultValues(orderDetail);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public Order getEntityById(UUID id) {
        return orderRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ORDER, id.toString()));
    }

    private void setDefaultValues(OrderDetail orderDetail) {
        if (orderDetail.getQuantity() == null) orderDetail.setQuantity(1);
        if (orderDetail.getPrice() == null) {
            orderDetail.setPrice(orderDetail.getProduct().getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
        }
    }
}
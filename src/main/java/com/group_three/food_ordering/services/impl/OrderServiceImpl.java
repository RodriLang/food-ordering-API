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
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.ProductService;
import com.group_three.food_ordering.utils.OrderServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
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
    private final TenantContext tenantContext;
    private final TableSessionRepository tableSessionRepository;
    private final OrderServiceHelper orderServiceHelper;

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {
        log.debug("[OrderService] Create Order Request");
        log.debug("[OrderService] Getting current food Venue.");
        FoodVenue currentFoodVenue = tenantContext.requireFoodVenue();
        log.debug("[OrderService] Getting current table session");
        TableSession tableSession = tenantContext.requireTableSession();
        log.debug("[OrderService] Getting current participant");
        Participant participant = tenantContext.requireParticipant();

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderNumber(orderServiceHelper.generateOrderNumber());
        order.setParticipant(participant);
        order.setStatus(OrderStatus.PENDING);
        order.setPublicId(UUID.randomUUID());
        order.setTableSession(tableSession);
        order.setOrderDate(Instant.now());

        // Revisar porque no permite cantidad de productos mayor a 1 como regla de negocio pero se puede evaluar
        order.setFoodVenue(currentFoodVenue);
        List<OrderDetail> orderDetails = orderRequestDto.getOrderDetails()
                .stream()
                .map(dto -> {
                    log.debug("[ProductService] Calling getEntityByNameAndContext for product: {}", dto.getProductName());
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
        log.debug("[OrderRepository] Calling save to create new order for participant {}", participant.getPublicId());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override

    public Page<OrderResponseDto> getOrdersByFilters(
            LocalDate from, LocalDate to, OrderStatus status, Pageable pageable) {
        Instant fromInstant = (from != null) ? from.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant toInstant = (to != null) ? to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        return fetchOrders(fromInstant, toInstant, status, pageable).map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesByFilters(LocalDate from, LocalDate to, OrderStatus status) {
        Instant fromInstant = (from != null) ? from.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        Instant toInstant = (to != null) ? to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;

        return fetchOrders(fromInstant, toInstant, status, Pageable.unpaged()).toList();
    }


    @Override
    public Page<OrderResponseDto> getOrdersForToday(OrderStatus status, Pageable pageable) {
        Instant opening = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant closing = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return fetchOrders(opening, closing, status, pageable).map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesForToday(OrderStatus orderStatus) {
        Instant opening = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant closing = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        return fetchOrders(opening, closing, null, Pageable.unpaged()).toList();
    }

    @Override
    public Page<OrderResponseDto> getOrdersByTableSessionAndStatus(UUID tableSessionId, OrderStatus status, Pageable pageable) {

        Participant currentParticipant = tenantContext.requireParticipant();

        TableSession session = getTableSessionEntityById(tableSessionId);

        RoleType role = tenantContext.getRole();

        if (role.equals(RoleType.ROLE_CLIENT)
                && !session.getParticipants().contains(currentParticipant)) {

            throw new LogicalAccessDeniedException("You do not have access to this table session");
        }
        //filtra por estado
        Page<Order> orders;
        if (status != null) {
            log.debug("[OrderRepository] Calling findOrderByTableSession_PublicIdAndStatus for " +
                    "tableSessionId={} and status={}", tableSessionId, status);
            orders = orderRepository.findOrderByTableSession_PublicIdAndStatus(tableSessionId, status, pageable);
        } else {
            log.debug("[OrderRepository] Calling findOrderByTableSession_PublicId for tableSessionId={}", tableSessionId);
            orders = orderRepository.findOrderByTableSession_PublicId(tableSessionId, pageable);
        }
        return orders.map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesByTableSessionAndStatus(UUID tableSessionId, OrderStatus orderStatus) {

        TableSession session = getTableSessionEntityById(tableSessionId);

        List<Order> orders;
        if (orderStatus != null) {
            log.debug("[OrderRepository] Calling findOrderByTableSession_PublicIdAndStatus or " +
                    "tableSessionId={} and status={}", session.getPublicId(), orderStatus);

            orders = orderRepository.findOrderByTableSession_PublicIdAndStatus(
                    session.getPublicId(), orderStatus, Pageable.unpaged()).toList();
        } else {
            log.debug("[OrderRepository] Calling findOrderByTableSession_PublicId (List) for tableSessionId={}", tableSessionId);
            orders = orderRepository.findOrderByTableSession_PublicId(tableSessionId, Pageable.unpaged()).toList();
        }
        return orders;
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClient(OrderStatus status, Pageable pageable) {
        User authenticatedClient = tenantContext.requireUser();
        return getOrdersByUser(authenticatedClient.getPublicId(), status, pageable);
    }

    private Page<OrderResponseDto> getOrdersByUser(UUID userId, OrderStatus status, Pageable pageable) {
        // Se filtra por estado
        Page<Order> orders;
        if (status != null) {
            log.debug("[OrderRepository] Calling findOrdersByParticipant_PublicIdAndStatus for userId={} and status={}", userId, status);
            orders = orderRepository.findOrdersByParticipant_PublicIdAndStatus(userId, status, pageable);
        } else {
            log.debug("[OrderRepository] Calling findOrdersByParticipant_PublicId for userId={}", userId);
            orders = orderRepository.findOrdersByParticipant_PublicId(userId, pageable);
        }

        return orders.map(orderMapper::toDto);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClientAndStatus(OrderStatus status, Pageable pageable) {
        log.debug("[OrderService] Get orders by authenticated user");
        UUID currentClientId = tenantContext.getParticipantId();
        return getOrdersByUser(currentClientId, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByAuthenticatedClientAndCurrentTableSessionAndStatus(
            OrderStatus status, Pageable pageable) {

        UUID currentClientId = tenantContext.getParticipantId();
        UUID currentTableSessionId = tenantContext.getTableSessionId();

        return getOrdersByClientAndTableSessionAndStatus(currentClientId, currentTableSessionId, status, pageable);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByClientAndTableSessionAndStatus(
            UUID clientId, UUID tableSessionId, OrderStatus status, Pageable pageable) {

        UUID currentClientId = tenantContext.getParticipantId();
        UUID currentTableSessionId = tenantContext.getTableSessionId();

        log.debug("[OrderRepository] Calling findOrdersByParticipant_PublicIdAndTableSession_PublicIdAndStatus for clientId={}, sessionId={}, status={}", currentClientId, currentTableSessionId, status);
        return orderRepository.findOrdersByParticipant_PublicIdAndTableSession_PublicIdAndStatus(
                currentClientId, currentTableSessionId, status, pageable).map(orderMapper::toDto);
    }

    @Override
    public Page<OrderResponseDto> getOrdersByCurrentParticipant(Pageable pageable) {
        log.debug("[OrderService] Get orders by current participant");
        UUID currentClientId = tenantContext.getParticipantId();
        log.debug("[OrderService] Current client ID={}", currentClientId);
        log.debug("[OrderRepository] Calling findOrdersByParticipant_PublicId for currentClientId={}", currentClientId);
        return orderRepository.findOrdersByParticipant_PublicId(currentClientId, pageable).map(orderMapper::toDto);
    }

    @Override
    public List<Order> getOrderEntitiesByCurrentParticipant() {
        log.debug("[OrderService] Get order entities by current participant");
        UUID currentClientId = tenantContext.getParticipantId();
        log.debug("[OrderRepository] Calling findOrdersByParticipant_PublicId (unpaged) for currentClientId={}", currentClientId);
        return orderRepository.findOrdersByParticipant_PublicId(currentClientId, Pageable.unpaged()).toList();
    }

    @Override
    public Integer reassignOrdersToParticipant(Participant guest, Participant existing){
        List<Order> orders = orderRepository.findOrdersByParticipant_PublicId(guest.getPublicId(), Pageable.unpaged()).toList();
        orders.forEach(order -> order.setParticipant(existing));
        orderRepository.saveAll(orders);
        return orders.size();
    }

    // permite recibir parámetros opcionalmente
// omitiendo el filtro que no fue especificado en la consulta
    private Page<Order> fetchOrders(
            Instant from,
            Instant to,
            OrderStatus status,
            Pageable pageable
    ) {
        UUID venueId = tenantContext.getFoodVenueId();
        Page<Order> orders;

        if (from != null && to != null) {
            if (status != null) {
                log.debug("[OrderRepository] Calling findByFoodVenue_PublicIdAndOrderDateBetweenAndStatus for venueId={}, from={}, to={}, status={}", venueId, from, to, status);
                orders = orderRepository.findByFoodVenue_PublicIdAndOrderDateBetweenAndStatus(venueId, from, to, status, pageable);
            } else {
                log.debug("[OrderRepository] Calling findByFoodVenue_PublicIdAndOrderDateBetween for venueId={}, from={}, to={}", venueId, from, to);
                orders = orderRepository.findByFoodVenue_PublicIdAndOrderDateBetween(venueId, from, to, pageable);
            }
        } else if (status != null) {
            log.debug("[OrderRepository] Calling findByFoodVenue_PublicIdAndStatus for venueId={}, status={}", venueId, status);
            orders = orderRepository.findByFoodVenue_PublicIdAndStatus(venueId, status, pageable);
        } else {
            log.debug("[OrderRepository] Calling findByFoodVenue_PublicId for venueId={}", venueId);
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
        log.debug("[OrderRepository] Calling save to update special requirements for order {}", orderId);
        orderRepository.save(existingOrder);

        return new OrderResponseDto();
    }

    @Override
    public void delete(UUID id) {
        Order order = this.getEntityById(id);
        order.setDeleted(Boolean.TRUE);
        log.debug("[OrderRepository] Calling save to soft delete order {}", id);
        orderRepository.save(order);
    }

    @Override
    public OrderResponseDto updateStatus(UUID id, OrderStatus orderStatus) {
        Order existingOrder = this.getEntityById(id);
        Participant participant = tenantContext.requireParticipant();
        UUID currentContext = tenantContext.getFoodVenueId();

        if (!existingOrder.getFoodVenue().getPublicId().equals(currentContext)) {
            throw new EntityNotFoundException(ORDER);
        }

        if ((participant.getRole().equals(RoleType.ROLE_CLIENT) || participant.getRole().equals(RoleType.ROLE_GUEST)) &&
                !existingOrder.getParticipant().getPublicId().equals(participant.getPublicId())) {
            throw new EntityNotFoundException(ORDER);
        }

        existingOrder.setStatus(orderStatus);
        log.debug("[OrderRepository] Calling save to update status to {} for order {}", orderStatus, id);
        orderRepository.save(existingOrder);

        return orderMapper.toDto(existingOrder);
    }

    @Override
    public OrderResponseDto getOrderByDateAndOrderNumber(
            LocalDate date, Integer orderNumber) {
        Instant start = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        UUID currentVenueId = tenantContext.getFoodVenueId();
        log.debug("[OrderRepository] Calling findByFoodVenue_PublicIdAndOrderNumberAndOrderDateBetween for " +
                "venueId={}, orderNumber={} and date range", currentVenueId, orderNumber);
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
        log.debug("[OrderRepository] Calling save to remove order detail from order {}", orderId);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        orderServiceHelper.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().add(orderDetail);
        orderServiceHelper.updateTotalPrice(existingOrder);
        this.setDefaultValues(orderDetail);
        log.debug("[OrderRepository] Calling save to add order detail to order {}", orderId);
        this.orderRepository.save(existingOrder);
    }

    @Override
    public Order getEntityById(UUID id) {
        log.debug("[OrderRepository] Calling findByPublicId for orderId={}", id);
        return orderRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ORDER, id.toString()));
    }

    private void setDefaultValues(OrderDetail orderDetail) {
        if (orderDetail.getQuantity() == null) orderDetail.setQuantity(1);
        if (orderDetail.getPrice() == null) {
            orderDetail.setPrice(orderDetail.getProduct().getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
        }
    }

    private TableSession getTableSessionEntityById(UUID tableSessionId) {
        log.debug("[TableSessionRepository] Calling findByPublicId for tableSessionId={}", tableSessionId);
        return tableSessionRepository.findByPublicId(tableSessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, tableSessionId.toString()));
    }
}
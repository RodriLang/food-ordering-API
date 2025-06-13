package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.AccessDeniedException;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.OrderInProgressException;
import com.group_three.food_ordering.mappers.OrderDetailMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.mappers.OrderMapper;
import com.group_three.food_ordering.repositories.*;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final TenantContext tenantContext;
    private final IClientRepository clientRepository;
    private final AuthService authService;
    private final ITableSessionRepository tableSessionRepository;

    @Override
    public OrderResponseDto create(OrderRequestDto orderRequestDto) {

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderNumber(this.generateOrderNumber());
        order.setClient(clientRepository.findById(orderRequestDto.getClientId()).orElseThrow(
                ()-> new EntityNotFoundException(Client.class.getSimpleName(), orderRequestDto.getClientId().toString())
        ));

        FoodVenue currentFoodVenue = tenantContext.getCurrentFoodVenue();

        /// method added for setting Table Session Id in the order
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
                    detail.setOrder(order);
                    detail.setQuantity(1);
                    detail.setPrice(product.getPrice());
                    return detail;
                })
                .toList();

        order.setOrderDetails(orderDetails);
        this.updateTotalPrice(order);
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

                throw new AccessDeniedException("You do not have access to this table session");
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

        // Se filtra por estado
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findOrdersByClient_Id(clientId);
        } else {
            orders = orderRepository.findOrdersByClient_IdAndStatus(clientId, status);
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
    public List<OrderDetailResponseDto> getOrderDetailsByOrderId(UUID orderId) {
        return orderDetailRepository.findAllByOrder_IdAndDeletedFalse(orderId)
                .stream()
                .map(orderDetailMapper::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDto getOrderByDateAndOrderNumber(
            LocalDate date, Integer orderNumber) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return orderMapper.toDTO(orderRepository
                .findByFoodVenue_IdAndOrderNumberAndCreationDateBetween(
                        tenantContext.getCurrentFoodVenue().getId(), orderNumber, start, end)
                .orElseThrow(()-> new EntityNotFoundException("Order not found")));
    }


    @Override
    public void removeOrderDetailFromOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        this.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().remove(orderDetail);
        updateTotalPrice(existingOrder);

        this.orderRepository.save(existingOrder);
    }

    @Override
    public void addOrderDetailToOrder(UUID orderId, OrderDetail orderDetail) {

        Order existingOrder = this.getEntityById(orderId);
        this.validateUpdate(existingOrder);
        existingOrder.getOrderDetails().add(orderDetail);
        updateTotalPrice(existingOrder);

        this.orderRepository.save(existingOrder);
    }

    @Override
    public Order getEntityById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(Order.class.getName() + id));
    }


    private void updateTotalPrice(Order order) {
        BigDecimal totalPrice = order.getOrderDetails().stream()
                .map(orderDetail
                        -> orderDetail.getPrice().multiply(
                        new BigDecimal(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
    }

    private void validateUpdate(Order order) {
        // La orden solo puede modificarse si está en estado PENDING, o si está en estado APPROVED
        // y su pago aún no ha sido realizado (es decir, el pago está en estado PENDING).
        if (order.getStatus() == OrderStatus.PENDING ||
                (order.getStatus() == OrderStatus.APPROVED &&
                        (order.getPayment() != null && order.getPayment().getStatus() == PaymentStatus.PENDING))) {
            // La orden puede ser modificada, no hacemos nada
            return;
        }

        // Si no cumple con las condiciones anteriores, lanzamos la excepción
        throw new OrderInProgressException(order.getId());
    }

    private Integer generateOrderNumber() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        int ordersCount = Math.toIntExact(orderRepository.countOrdersToday(
                tenantContext.getCurrentFoodVenueId(), start, end));

        return ordersCount + 1;
    }

}

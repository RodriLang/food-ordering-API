package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.ParticipantController;
import com.group_three.food_ordering.dto.response.*;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.PaymentService;
import com.group_three.food_ordering.services.TableSessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@PreAuthorize("hasAnyRole('CLIENT', 'GUEST')")
@RestController
@RequiredArgsConstructor
@Tag(name = "Participants", description = "Acciones de los participantes de una sesión de mesa.")
public class ParticipantControllerImpl implements ParticipantController {

    private final OrderService orderService;
    private final ParticipantService participantService;
    private final PaymentService paymentService;
    private final TableSessionService tableSessionService;

    @Override
    public ResponseEntity<PageResponse<OrderResponseDto>> getCurrentOrders(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(orderService.getOrdersByCurrentParticipant(pageable)));
    }

    @Override
    public ResponseEntity<AuthResponse> delegateHostingDuties(UUID participantId) {
        return ResponseEntity.ok(participantService.delegateHostingDuties(participantId));
    }

    @Override
    public ResponseEntity<PageResponse<PaymentResponseDto>> getAllPaymentsByCurrentTableSessionAndStatus(
            PaymentStatus status, Pageable pageable) {

        Page<PaymentResponseDto> payments =
                paymentService.getAllByCurrentTableSessionAndStatus(status, pageable);
        return ResponseEntity.ok(PageResponse.of(payments));
    }

    @Override
    public ResponseEntity<PageResponse<PaymentResponseDto>> getAllOwnPayments(
            PaymentStatus status, Pageable pageable) {

        Page<PaymentResponseDto> payments =
                paymentService.getAllOwnPaymentsAndStatus(status, pageable);
        return ResponseEntity.ok(PageResponse.of(payments));
    }

    @Override
    public ResponseEntity<PageResponse<OrderResponseDto>> getAllOrdersByCurrentTableSessionAndStatus(OrderStatus status, Pageable pageable) {
        Page<OrderResponseDto> orders =
                orderService.getAllOrdersByCurrentTableSessionAndStatus(status, pageable);
        return ResponseEntity.ok(PageResponse.of(orders));
    }

    @Override
    public ResponseEntity<TableSessionResponseDto> getCurrentTableSession() {
        return ResponseEntity.ok(tableSessionService.getByCurrentParticipant());
    }

    @Override
    public ResponseEntity<Void> endYourOwnTableSession() {
        tableSessionService.closeCurrentSession();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthResponse> leaveTableSession() {
        tableSessionService.leaveCurrentSession();
        return ResponseEntity.noContent().build();
    }
}

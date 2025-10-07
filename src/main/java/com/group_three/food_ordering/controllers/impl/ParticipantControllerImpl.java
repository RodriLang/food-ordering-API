package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.ParticipantController;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Participants", description = "Gestión de clientes registrados e invitados.")
public class ParticipantControllerImpl implements ParticipantController {

    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_GUEST')")
    @Override
    public ResponseEntity<Page<OrderResponseDto>> getCurrentOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByCurrentParticipant(pageable));
    }
}

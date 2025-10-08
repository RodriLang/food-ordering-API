package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.ParticipantController;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.ParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Participants", description = "Acciones de los participantes de una sesión de mesa.")
public class ParticipantControllerImpl implements ParticipantController {

    private final OrderService orderService;
    private final ParticipantService participantService;

    @PreAuthorize("hasAnyRole('CLIENT', 'GUEST')")
    @Override
    public ResponseEntity<Page<OrderResponseDto>> getCurrentOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByCurrentParticipant(pageable));
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'GUEST')")
    @Override
    public ResponseEntity<LoginResponse> delegateHostingDuties(UUID participantId) {
        return ResponseEntity.ok(participantService.delegateHostingDuties(participantId));
    }
}

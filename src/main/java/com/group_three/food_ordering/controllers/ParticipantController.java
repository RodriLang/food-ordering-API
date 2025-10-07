package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.PARTICIPANT_URI)
@Tag(name = "Participantes", description = "Gestión de clientes registrados e invitados asociados a una sesión de mesa.")
public interface ParticipantController {

    @Operation(
            summary = "Obtener todas las órdenes",
            description = "Devuelve la lista con los pedidos realizados por participante autenticado o invitado.",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_GUEST')")
    @GetMapping("/orders")
    ResponseEntity<Page<OrderResponseDto>> getCurrentOrders(@Parameter Pageable pageable);

}

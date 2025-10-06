package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(ApiPaths.CURRENT_URI)
@Tag(name = "Usuario autenticado", description = "Acciones del usuario que se encuentra usando la aplicación")
public interface CurrentUserController {

    @GetMapping("/user")
    ResponseEntity<UserResponseDto> getAuthenticatedUser();


    @GetMapping("/orders")
    ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(hidden = true) Pageable pageable);


    @PostMapping("/table-sessions")
    ResponseEntity<InitSessionResponseDto> createTableSession(@RequestBody @Valid TableSessionRequestDto tableSessionRequestDto);


    @GetMapping("/table-sessions")
    ResponseEntity<Page<OrderResponseDto>> getMyCurrentTableSessionOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesiones del cliente como anfitrión",
            description = "Devuelve todas las sesiones donde el usuario actual es el anfitrión.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones por cliente anfitrión",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table-session/host")
     ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByAuthUserHostClient();


    @Operation(
            summary = "Obtener sesiones pasadas del participante actual",
            description = "Devuelve todas las sesiones pasadas en las que el cliente autenticado participó.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones pasadas por participante",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table-session/participant")
     ResponseEntity<List<TableSessionResponseDto>> getPastTableSessionsByAuthUserParticipant();

}

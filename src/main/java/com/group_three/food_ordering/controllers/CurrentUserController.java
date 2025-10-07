package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.CURRENT_URI)
@Tag(name = "Usuario autenticado", description = "Acciones del usuario que se encuentra registrado la aplicación")
public interface CurrentUserController {

    @Operation(
            summary = "Obtener los datos del usuario autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    @GetMapping("/profile")
    ResponseEntity<UserResponseDto> getAuthenticatedUser();


    @Operation(
            summary = "Modificar datos personales",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario modificado con éxito",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    @PatchMapping("/profile")
    ResponseEntity<UserResponseDto> updateUser(@RequestBody @Validated(OnUpdate.class) UserRequestDto dto);


    @Operation(
            summary = "Eliminar usuario",
            responses = {@ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
            }
    )
    @DeleteMapping("/profile")
    ResponseEntity<UserResponseDto> deleteUser();


    @GetMapping("/orders")
    ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(hidden = true) Pageable pageable);


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
    @GetMapping("/table-sessions/host")
    ResponseEntity<Page<TableSessionResponseDto>> getTableSessionsByAuthUserHostClient(
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesiones pasadas del participante actual",
            description = "Devuelve todas las sesiones pasadas en las que el cliente autenticado participó.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones pasadas por participante",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table-sessions/participant")
    ResponseEntity<Page<TableSessionResponseDto>> getPastTableSessionsByAuthUserParticipant(
            @Parameter(hidden = true) Pageable pageable);

}

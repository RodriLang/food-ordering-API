package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
                            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
            }
    )
    @GetMapping("/profile")
    ResponseEntity<UserDetailResponseDto> getAuthenticatedUser();


    @Operation(
            summary = "Modificar datos personales",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario modificado con éxito",
                            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
            }
    )
    @PatchMapping("/profile")
    ResponseEntity<UserDetailResponseDto> updateUser(@RequestBody @Validated(OnUpdate.class) UserRequestDto dto);


    @Operation(
            summary = "Eliminar usuario",
            responses = {@ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
            }
    )
    @DeleteMapping("/profile")
    ResponseEntity<UserDetailResponseDto> deleteUser();

    @Operation(
            summary = "Obtener todos los pedidos realizados",
            description = "devuelve todas las ordenes del usuario autenticado sin importar el lugar de comida actual",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
            }
    )
    @GetMapping("/orders")
    ResponseEntity<PageResponse<OrderResponseDto>> getMyOrders(
            @RequestParam(required = false) OrderStatus orderStatus,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener todos los pedidos de la sesión de mesa",
            description = "devuelve todas las ordenes del usuario autenticado en su sesión actualgit ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
            }
    )
    @GetMapping("/table-sessions")
    ResponseEntity<PageResponse<OrderResponseDto>> getMyCurrentTableSessionOrders(
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
    ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByAuthUserHostClient(
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
    ResponseEntity<PageResponse<TableSessionResponseDto>> getPastTableSessionsByAuthUserParticipant(
            @Parameter(hidden = true) Pageable pageable);

}

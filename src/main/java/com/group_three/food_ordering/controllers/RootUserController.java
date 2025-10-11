package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.UserDetailResponseDto;
import com.group_three.food_ordering.utils.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.ROOT_USER_URI)
@Tag(name = "Control de Usuarios", description = "Operaciones privadas relacionadas con usuarios del sistema")
public interface RootUserController {

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve un usuario por su ID si no fue eliminado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    ResponseEntity<UserDetailResponseDto> getById(@PathVariable UUID id);

    @GetMapping("/all")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios registrados, incluyendo los eliminados."
    )
    ResponseEntity<PageResponse<UserDetailResponseDto>> getAll(@Parameter(hidden = true) Pageable pageable);

    @GetMapping("/actives")
    @Operation(
            summary = "Listar usuarios activos",
            description = "Devuelve todos los usuarios que no han sido eliminados (removedAt es null)."
    )
    ResponseEntity<PageResponse<UserDetailResponseDto>> getActives(@Parameter(hidden = true) Pageable pageable);

    @GetMapping("/deleted")
    @Operation(
            summary = "Listar usuarios eliminados",
            description = "Devuelve todos los usuarios que han sido marcados como eliminados (removedAt no es null)."
    )
    ResponseEntity<PageResponse<UserDetailResponseDto>> getDeleted(@Parameter(hidden = true) Pageable pageable);

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar completamente un usuario",
            description = "Reemplaza todos los datos del usuario. Se requiere proporcionar todos los campos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    ResponseEntity<UserDetailResponseDto> updateById(
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @RequestBody UserRequestDto dto);

    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar parcialmente un usuario",
            description = "Modifica uno o varios campos del usuario. Similar al PUT, pero parcial.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado parcialmente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    ResponseEntity<UserDetailResponseDto> patchUserById(
            @PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UserRequestDto dto);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar lógicamente un usuario",
            description = "Marca el usuario como eliminado estableciendo su campo removedAt."
    )
    ResponseEntity<Void> deleteById(@PathVariable UUID id);

}

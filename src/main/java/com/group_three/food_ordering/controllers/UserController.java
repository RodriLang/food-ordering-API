package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;
import com.group_three.food_ordering.services.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.USER_BASE)
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operaciones relacionadas con usuarios del sistema (Staff, Clientes, Invitados)")
public class UserController {

    private final IUserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Crea un usuario con todos sus datos. El correo electrónico debe ser único.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    public UserResponseDto create(
            @Valid @org.springframework.web.bind.annotation.RequestBody UserCreateDto dto) {
        return userService.create(dto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve un usuario por su ID si no fue eliminado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    public UserResponseDto getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios registrados, incluyendo los eliminados."
    )
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/actives")
    @Operation(
            summary = "Listar usuarios activos",
            description = "Devuelve todos los usuarios que no han sido eliminados (removedAt es null)."
    )
    public List<UserResponseDto> getActives() {
        return userService.getActiveUsers();
    }

    @GetMapping("/deleted")
    @Operation(
            summary = "Listar usuarios eliminados",
            description = "Devuelve todos los usuarios que han sido marcados como eliminados (removedAt no es null)."
    )
    public List<UserResponseDto> getDeleted() {
        return userService.getDeletedUsers();
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar completamente un usuario",
            description = "Reemplaza todos los datos del usuario. Se requiere proporcionar todos los campos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    public UserResponseDto update(
            @PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateDto dto) {
        return userService.update(id, dto);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar parcialmente un usuario",
            description = "Modifica uno o varios campos del usuario. Similar al PUT, pero parcial.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado parcialmente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    public UserResponseDto patchUser(
            @PathVariable UUID id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UserUpdateDto dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar lógicamente un usuario",
            description = "Marca el usuario como eliminado estableciendo su campo removedAt."
    )
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}

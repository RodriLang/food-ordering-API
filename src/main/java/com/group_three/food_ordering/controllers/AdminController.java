package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(ApiPaths.ADMIN_URI)
@Tag(name = "Users", description = "Operaciones relacionadas con usuarios administradores del sistema")
public interface AdminController {

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo usuario administrador",
            description = "Crea un admin vinculando a un User existente con un Lugar de comida.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Admin creado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario o Lugar de comida no encontrados"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    ResponseEntity<UserResponseDto> registerAdmin(
            @Valid @RequestBody UserCreateDto dto);

    @GetMapping("/root/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve un usuario por su ID si no fue eliminado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    ResponseEntity<UserResponseDto> getById(@PathVariable UUID id);

    @GetMapping("/root/all")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios registrados, incluyendo los eliminados."
    )
    ResponseEntity<List<UserResponseDto>> getAll();

    @GetMapping("/root/actives")
    @Operation(
            summary = "Listar usuarios activos",
            description = "Devuelve todos los usuarios que no han sido eliminados (removedAt es null)."
    )
    ResponseEntity<List<UserResponseDto>> getActives();

    @GetMapping("/root/deleted")
    @Operation(
            summary = "Listar usuarios eliminados",
            description = "Devuelve todos los usuarios que han sido marcados como eliminados (removedAt no es null)."
    )
    ResponseEntity<List<UserResponseDto>> getDeleted();

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar completamente un usuario",
            description = "Reemplaza todos los datos del usuario. Se requiere proporcionar todos los campos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    ResponseEntity<UserResponseDto> updateById(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDto dto);

    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar parcialmente un usuario",
            description = "Modifica uno o varios campos del usuario. Similar al PUT, pero parcial.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado parcialmente"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    ResponseEntity<UserResponseDto> patchUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDto dto);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar lógicamente un usuario administrador",
            description = "Marca el usuario como eliminado estableciendo su campo removedAt."
    )
    ResponseEntity<Void> deleteById(@PathVariable UUID id);

}

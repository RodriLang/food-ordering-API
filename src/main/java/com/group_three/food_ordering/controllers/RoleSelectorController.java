package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.RoleSelectionRequestDto;
import com.group_three.food_ordering.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(ApiPaths.ROLE_SELECTOR_URI)
public interface RoleSelectorController {

    @Operation(
            summary = "Seleccionar rol",
            description = "Permite elegir entre los roles disponibles al momento de loguearse.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selección exitosa"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
            }
    )
    @PostMapping("/select")
    ResponseEntity<LoginResponse> select(@RequestBody RoleSelectionRequestDto request);


    @Operation(
            summary = "Seleccionar rol cliente",
            description = "Permite volver al rol por defecto para operar como cliente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selección exitosa"),
            }
    )
    @PostMapping("/client")
    ResponseEntity<LoginResponse> client();
}
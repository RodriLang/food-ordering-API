package com.group_three.food_ordering.qrcode.controller;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.qrcode.dto.request.GenerateQrCodeRequestDto;
import com.group_three.food_ordering.qrcode.dto.response.GenerateQrCodeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(ApiPaths.TABLE_URI)
@Tag(name = "QR Codes", description = "Generación de códigos QR para mesas")
public interface QrCodeController {

    @Operation(
            summary = "Generar QR code para una mesa",
            description = "Genera un código QR para una mesa específica que redirige a la URL base proporcionada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "QR code generado exitosamente",
                            content = @Content(schema = @Schema(implementation = GenerateQrCodeResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @PostMapping("/generate-qr-code")
    ResponseEntity<GenerateQrCodeResponseDto> generateQrCode(
            @Valid @RequestBody GenerateQrCodeRequestDto request);
}

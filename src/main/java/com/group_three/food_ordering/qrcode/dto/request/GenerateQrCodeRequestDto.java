package com.group_three.food_ordering.qrcode.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request para generar QR code de mesa")
public record GenerateQrCodeRequestDto(

        @NotBlank(message = "Base URL is required")
        @Schema(description = "URL base donde redirigirá el QR", example = "http://localhost:4200/#/scan-qr")
        String baseUrl,

        @NotNull(message = "Table number is required")
        @Schema(description = "Número de mesa", example = "9")
        Integer tableNumber
) {}

package com.group_three.food_ordering.qrcode.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de generación de QR code")
public record GenerateQrCodeResponseDto(

        @Schema(description = "URL del QR code generado",
                example = "https://res.cloudinary.com/example/qr-codes/table-123.png")
        String qrCodeUrl
) {}

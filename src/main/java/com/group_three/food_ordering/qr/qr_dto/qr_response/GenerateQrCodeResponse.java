package com.group_three.food_ordering.qr.qr_dto.qr_response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para generar QR code de mesa")
public record GenerateQrCodeResponse(

        String qrCodeUrl

) {
}

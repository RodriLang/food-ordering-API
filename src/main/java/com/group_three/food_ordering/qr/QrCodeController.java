package com.group_three.food_ordering.qr;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.qr.qr_dto.qr_request.GenerateQrCodeRequest;
import com.group_three.food_ordering.qr.qr_dto.qr_response.GenerateQrCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ApiPaths.TABLE_URI)
@RequiredArgsConstructor
@Tag(name = "QR Codes", description = "Generación de códigos QR para mesas")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @Operation(summary = "Generar QR code para una mesa")
    @ApiResponse(responseCode = "200", description = "QR code generado exitosamente")
    @PostMapping("/generate-qr-code")
    public ResponseEntity<GenerateQrCodeResponse> generateQrCode(
            @Valid @RequestBody GenerateQrCodeRequest request) {

        log.info("POST /api/v1/tables/generate-qr-code - table: {}, baseUrl: {}",
                request.tableNumber(), request.baseUrl());

        return ResponseEntity.ok(qrCodeService.generateTableQrCode(request));
    }
}
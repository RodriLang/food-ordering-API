package com.group_three.food_ordering.qr;

import com.group_three.food_ordering.configs.ApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(ApiPaths.TABLE_URI)
@RequiredArgsConstructor
@Tag(name = "QR Codes", description = "Generación de códigos QR para mesas")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @Operation(summary = "Generar QR code para una mesa")
    @ApiResponse(responseCode = "200", description = "QR code generado exitosamente")
    @PostMapping("/{tableId}/generate-qr-code")
    public ResponseEntity<Map<String, String>> generateQrCode(@PathVariable UUID tableId) {
        log.info("POST /api/v1/tables/{}/generate-qr-code", tableId);

        String qrCodeUrl = qrCodeService.generateTableQrCode(tableId);

        return ResponseEntity.ok(Map.of(
                "qrCodeUrl", qrCodeUrl,
                "message", "QR code generated successfully"
        ));
    }
}
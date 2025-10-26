package com.group_three.food_ordering.qrcode.controller.impl;

import com.group_three.food_ordering.qrcode.controller.QrCodeController;
import com.group_three.food_ordering.qrcode.dto.request.GenerateQrCodeRequestDto;
import com.group_three.food_ordering.qrcode.dto.response.GenerateQrCodeResponseDto;
import com.group_three.food_ordering.qrcode.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QrCodeControllerImpl implements QrCodeController {

    private final QrCodeService qrCodeService;

    @PreAuthorize("hasAnyRole('ADMIN','ROOT')")
    @Override
    public ResponseEntity<GenerateQrCodeResponseDto> generateQrCode(GenerateQrCodeRequestDto request) {
        return ResponseEntity.ok(qrCodeService.generateTableQrCode(request));
    }
}
package com.group_three.food_ordering.qrcode.service;

import com.group_three.food_ordering.qrcode.dto.request.GenerateQrCodeRequestDto;
import com.group_three.food_ordering.qrcode.dto.response.GenerateQrCodeResponseDto;

public interface QrCodeService {

    GenerateQrCodeResponseDto generateTableQrCode(GenerateQrCodeRequestDto request);
}
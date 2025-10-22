package com.group_three.food_ordering.qr;

import com.group_three.food_ordering.qr.qr_dto.qr_request.GenerateQrCodeRequest;

public interface QrCodeService {

    String generateTableQrCode(GenerateQrCodeRequest request);
}
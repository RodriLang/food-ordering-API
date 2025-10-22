package com.group_three.food_ordering.qr;

import com.group_three.food_ordering.qr.qr_dto.qr_request.GenerateQrCodeRequest;
import com.group_three.food_ordering.qr.qr_dto.qr_response.GenerateQrCodeResponse;

public interface QrCodeService {

    GenerateQrCodeResponse generateTableQrCode(GenerateQrCodeRequest request);
}
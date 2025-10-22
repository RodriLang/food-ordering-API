package com.group_three.food_ordering.qr;

import java.util.UUID;

public interface QrCodeService {

    String generateTableQrCode(UUID tableId);
}
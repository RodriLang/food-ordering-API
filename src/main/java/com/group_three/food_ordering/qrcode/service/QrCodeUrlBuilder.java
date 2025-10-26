
package com.group_three.food_ordering.qrcode.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class QrCodeUrlBuilder {

    public String buildTableUrl(String baseUrl, UUID tableId) {
        // Remover trailing slash si existe
        String cleanBaseUrl = baseUrl.replaceAll("/$", "");

        // Simplemente agregar el UUID al final
        String url = String.format("%s/%s", cleanBaseUrl, tableId);

        log.debug("[QrCodeUrlBuilder] Built URL: {}", url);
        return url;
    }
}
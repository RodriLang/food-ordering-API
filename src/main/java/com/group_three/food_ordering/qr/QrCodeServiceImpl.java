package com.group_three.food_ordering.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.DiningTableRepository;
import com.group_three.food_ordering.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.DINING_TABLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final CloudinaryService cloudinaryService;
    private final DiningTableRepository tableRepository;
    private final TenantContext tenantContext;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;

    @Override
    @Transactional
    public String generateTableQrCode(UUID tableId) {
        log.info("[QrCodeService] Generating QR code for table {}", tableId);

        // 1. Solo verificamos que la mesa existe
        if (!tableRepository.existsByPublicIdAndDeletedFalse(tableId)) {
            throw new EntityNotFoundException(DINING_TABLE, tableId.toString());
        }

        // 2. Obtener el venue actual
        FoodVenue venue = tenantContext.requireFoodVenue();

        // 3. Construir la URL que va a tener el QR
        String tableUrl = buildTableUrl(venue, tableId);
        log.debug("[QrCodeService] QR will redirect to: {}", tableUrl);

        // 4. Generar el QR code como bytes
        byte[] qrCodeBytes = generateQrCodeImage(tableUrl);

        // 5. Subir a Cloudinary
        String identifier = "table-" + tableId;
        String qrCodeUrl = uploadQrCodeToCloudinary(qrCodeBytes, venue.getName(), identifier);

        // 6. Update directo sin cargar la entidad completa
        tableRepository.updateQrCodeUrl(tableId, qrCodeUrl);

        log.info("[QrCodeService] QR code generated and saved: {}", qrCodeUrl);

        return qrCodeUrl;
    }

    /**
     * Genera la imagen del QR code como array de bytes
     */
    private byte[] generateQrCodeImage(String content) {
        try {
            log.debug("[QrCodeService] Generating QR image for content length: {}", content.length());

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("[QrCodeService] Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Sube el QR code a Cloudinary usando el CloudinaryService existente
     */
    private String uploadQrCodeToCloudinary(byte[] qrCodeBytes, String venueName, String identifier) {
        log.debug("[QrCodeService] Uploading QR to Cloudinary for venue: {}", venueName);
        return cloudinaryService.uploadQrCode(qrCodeBytes, venueName, identifier);
    }

    /**
     * Construye la URL del frontend que el QR va a abrir
     */
    private String buildTableUrl(FoodVenue venue, UUID tableId) {
        String sanitizedVenueName = venue.getName()
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");

        return String.format("%s/venue/%s/table/%s",
                frontendUrl,
                sanitizedVenueName,
                tableId);
    }
}
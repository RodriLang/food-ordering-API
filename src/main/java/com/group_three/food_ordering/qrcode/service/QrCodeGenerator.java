package com.group_three.food_ordering.qrcode.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.group_three.food_ordering.exceptions.QrCodeGeneratorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
public class QrCodeGenerator {

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    private static final int TOP_LABEL_HEIGHT = 60;
    private static final int BOTTOM_LABEL_HEIGHT = 60;
    private static final int QR_FRAME_THICKNESS = 8;
    private static final int QR_FRAME_PADDING = 8;
    private static final int SIDE_MARGIN = 20;
    private static final int QR_SECTION_WIDTH = QR_WIDTH + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2 + (SIDE_MARGIN * 2);
    private static final int TOTAL_HEIGHT = TOP_LABEL_HEIGHT + QR_HEIGHT + BOTTOM_LABEL_HEIGHT + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2;

    // Tamaño del logo como porcentaje del QR (recomendado: 20-30%)
    private static final double LOGO_SIZE_RATIO = 0.25;
    private static final int LOGO_BORDER_SIZE = 2; // Borde blanco mínimo (2px)

    public byte[] generateQrCodeWithLabels(String content, String topLabel, String bottomLabel) {
        return generateQrCodeWithLabels(content, topLabel, bottomLabel, null);
    }

    public byte[] generateQrCodeWithLabels(String content, String topLabel, String bottomLabel, String logoUrl) {
        try {
            log.debug("[QrCodeGenerator] Generating QR with labels - top: {}, bottom: {}, logo: {}",
                    topLabel, bottomLabel, logoUrl != null ? "YES" : "NO");

            BitMatrix matrix = generateQrMatrix(content);
            BufferedImage combinedImage = createBlankImage();
            Graphics2D graphics = setupGraphics(combinedImage);

            drawTopLabel(graphics, topLabel);
            drawQrFrameAndCode(graphics, matrix, logoUrl);
            drawBottomLabel(graphics, bottomLabel);

            graphics.dispose();

            return convertToBytes(combinedImage);

        } catch (Exception e) {
            log.error("[QrCodeGenerator] Error generating QR code", e);
            throw new QrCodeGeneratorException("Failed to generate QR code with labels", e);
        }
    }

    private BitMatrix generateQrMatrix(String content) {
        QRCodeWriter writer = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);
        // Importante: usar nivel de corrección de errores ALTO para soportar el logo
        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);

        try {
            return writer.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

        } catch (Exception e) {
            log.error("[QrCodeGenerator] Error generating QR matrix", e);
            throw new QrCodeGeneratorException("Failed to generate QR matrix", e);
        }
    }

    private BufferedImage createBlankImage() {
        return new BufferedImage(QR_SECTION_WIDTH, TOTAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    private Graphics2D setupGraphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, QR_SECTION_WIDTH, TOTAL_HEIGHT);

        return graphics;
    }

    private void drawTopLabel(Graphics2D graphics, String label) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 32));

        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int textX = (QR_SECTION_WIDTH - textWidth) / 2;
        int textY = 40;

        graphics.drawString(label, textX, textY);
    }

    private void drawQrFrameAndCode(Graphics2D graphics, BitMatrix matrix, String logoUrl) {
        int qrAreaX = (QR_SECTION_WIDTH - QR_WIDTH - (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2) / 2;
        int qrAreaY = TOP_LABEL_HEIGHT;
        int qrAreaWidth = QR_WIDTH + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2;
        int qrAreaHeight = QR_HEIGHT + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2;

        graphics.setColor(Color.WHITE);
        graphics.fillRect(qrAreaX, qrAreaY, qrAreaWidth, qrAreaHeight);

        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(QR_FRAME_THICKNESS));
        graphics.drawRect(
                qrAreaX + QR_FRAME_THICKNESS / 2,
                qrAreaY + QR_FRAME_THICKNESS / 2,
                qrAreaWidth - QR_FRAME_THICKNESS,
                qrAreaHeight - QR_FRAME_THICKNESS
        );

        // Dibujar el QR code
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);
        int qrX = qrAreaX + QR_FRAME_THICKNESS + QR_FRAME_PADDING;
        int qrY = qrAreaY + QR_FRAME_THICKNESS + QR_FRAME_PADDING;
        graphics.drawImage(qrImage, qrX, qrY, null);

        // Dibujar el logo en el centro si se proporciona
        if (logoUrl != null && !logoUrl.trim().isEmpty()) {
            drawLogoOnQr(graphics, qrX, qrY, logoUrl);
        }
    }

    private void drawLogoOnQr(Graphics2D graphics, int qrX, int qrY, String logoUrl) {
        try {
            log.debug("[QrCodeGenerator] Loading logo from URL: {}", logoUrl);

            // Descargar la imagen del logo usando URI (Java 20+)
            URI logoUri = URI.create(logoUrl);
            BufferedImage logo = ImageIO.read(logoUri.toURL());

            if (logo == null) {
                log.warn("[QrCodeGenerator] Could not load logo from URL: {}", logoUrl);
                return;
            }

            // Calcular tamaño máximo del logo manteniendo aspect ratio
            int maxLogoSize = (int) (QR_WIDTH * LOGO_SIZE_RATIO);

            int originalWidth = logo.getWidth();
            int originalHeight = logo.getHeight();

            int logoWidth;
            int logoHeight;

            // Mantener aspect ratio
            if (originalWidth > originalHeight) {
                logoWidth = maxLogoSize;
                logoHeight = (int) ((double) originalHeight / originalWidth * maxLogoSize);
            } else {
                logoHeight = maxLogoSize;
                logoWidth = (int) ((double) originalWidth / originalHeight * maxLogoSize);
            }

            // Calcular posición central
            int logoX = qrX + (QR_WIDTH - logoWidth) / 2;
            int logoY = qrY + (QR_HEIGHT - logoHeight) / 2;

            // Dibujar fondo blanco sutil con esquinas redondeadas
            int backgroundWidth = logoWidth + (LOGO_BORDER_SIZE * 2);
            int backgroundHeight = logoHeight + (LOGO_BORDER_SIZE * 2);
            int backgroundX = logoX - LOGO_BORDER_SIZE;
            int backgroundY = logoY - LOGO_BORDER_SIZE;

            graphics.setColor(Color.WHITE);
            graphics.fillRoundRect(backgroundX, backgroundY, backgroundWidth, backgroundHeight, 10, 10);

            // Redimensionar y dibujar el logo manteniendo aspect ratio
            Image scaledLogo = logo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            graphics.drawImage(scaledLogo, logoX, logoY, null);

            log.debug("[QrCodeGenerator] Logo successfully added to QR code ({}x{})", logoWidth, logoHeight);

        } catch (Exception e) {
            log.warn("[QrCodeGenerator] Could not add logo to QR code: {}", e.getMessage());
            // No lanzar excepción, el QR sigue siendo funcional sin logo
        }
    }

    private void drawBottomLabel(Graphics2D graphics, String label) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 24));

        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int textX = (QR_SECTION_WIDTH - textWidth) / 2;
        int textY = TOP_LABEL_HEIGHT + QR_HEIGHT + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2 + 40;

        graphics.drawString(label, textX, textY);
    }

    private byte[] convertToBytes(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", outputStream);
        } catch (Exception e) {
            log.error("[QrCodeGenerator] Error converting image to bytes", e);
            throw new QrCodeGeneratorException("Failed to convert QR code image to bytes", e);
        }
        return outputStream.toByteArray();
    }
}
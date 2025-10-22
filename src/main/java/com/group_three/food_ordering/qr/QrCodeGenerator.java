package com.group_three.food_ordering.qr;

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
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
public class QrCodeGenerator {

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;
    private static final int TOP_LABEL_HEIGHT = 60;
    private static final int BOTTOM_LABEL_HEIGHT = 60;
    private static final int TOTAL_HEIGHT = TOP_LABEL_HEIGHT + QR_HEIGHT + BOTTOM_LABEL_HEIGHT;

    public byte[] generateQrCodeWithLabels(String content, String topLabel, String bottomLabel) {
        try {
            log.debug("[QrCodeGenerator] Generating QR with labels - top: {}, bottom: {}", topLabel, bottomLabel);

            BitMatrix matrix = generateQrMatrix(content);
            BufferedImage combinedImage = createBlankImage();
            Graphics2D graphics = setupGraphics(combinedImage);

            drawTopLabel(graphics, topLabel);
            drawQrCode(graphics, matrix);
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
        hints.put(EncodeHintType.MARGIN, 1); // Margin mínimo
        try {
            return writer.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

        } catch (Exception e) {
            log.error("[QrCodeGenerator] Error generating QR matrix", e);
            throw new QrCodeGeneratorException("Failed to generate QR matrix", e);
        }

    }

    private BufferedImage createBlankImage() {
        return new BufferedImage(QR_WIDTH, TOTAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    private Graphics2D setupGraphics(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo blanco
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, QR_WIDTH, TOTAL_HEIGHT);
        graphics.setColor(Color.BLACK);

        return graphics;
    }

    private void drawTopLabel(Graphics2D graphics, String label) {
        graphics.setFont(new Font("Arial", Font.BOLD, 32));

        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int textX = (QR_WIDTH - textWidth) / 2;
        int textY = 40;

        graphics.drawString(label, textX, textY);
    }

    private void drawQrCode(Graphics2D graphics, BitMatrix matrix) {
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);
        graphics.drawImage(qrImage, 0, TOP_LABEL_HEIGHT, null);
    }

    private void drawBottomLabel(Graphics2D graphics, String label) {
        graphics.setFont(new Font("Arial", Font.BOLD, 24));

        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int textX = (QR_WIDTH - textWidth) / 2;
        int textY = TOP_LABEL_HEIGHT + QR_HEIGHT + 40;

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

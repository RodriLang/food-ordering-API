package com.group_three.food_ordering.qrcode.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
    private static final int QR_FRAME_THICKNESS = 8; // Marco solo alrededor del QR
    private static final int QR_FRAME_PADDING = 8; // Espacio entre marco y QR
    private static final int SIDE_MARGIN = 20; // Margen lateral
    private static final int QR_SECTION_WIDTH = QR_WIDTH + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2 + (SIDE_MARGIN * 2);
    private static final int TOTAL_HEIGHT = TOP_LABEL_HEIGHT + QR_HEIGHT + BOTTOM_LABEL_HEIGHT + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2;

    public byte[] generateQrCodeWithLabels(String content, String topLabel, String bottomLabel) {
        try {
            log.debug("[QrCodeGenerator] Generating QR with labels and frame - top: {}, bottom: {}", topLabel, bottomLabel);

            BitMatrix matrix = generateQrMatrix(content);
            BufferedImage combinedImage = createBlankImage();
            Graphics2D graphics = setupGraphics(combinedImage);

            drawTopLabel(graphics, topLabel);
            drawQrFrameAndCode(graphics, matrix);
            drawBottomLabel(graphics, bottomLabel);

            graphics.dispose();

            return convertToBytes(combinedImage);

        } catch (Exception e) {
            log.error("[QrCodeGenerator] Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code with labels", e);
        }
    }

    private BitMatrix generateQrMatrix(String content) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);

        return writer.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
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

        // Fondo blanco
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

    private void drawQrFrameAndCode(Graphics2D graphics, BitMatrix matrix) {
        int qrAreaX = (QR_SECTION_WIDTH - QR_WIDTH - (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2) / 2;
        int qrAreaY = TOP_LABEL_HEIGHT;
        int qrAreaWidth = QR_WIDTH + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2;
        int qrAreaHeight = QR_HEIGHT + (QR_FRAME_THICKNESS + QR_FRAME_PADDING) * 2;

        // Fondo blanco del área del marco
        graphics.setColor(Color.WHITE);
        graphics.fillRect(qrAreaX, qrAreaY, qrAreaWidth, qrAreaHeight);

        // Marco negro grueso alrededor del QR
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

    private byte[] convertToBytes(BufferedImage image) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
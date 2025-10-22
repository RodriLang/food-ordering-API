package com.group_three.food_ordering.qr;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.DiningTable;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.qr.qr_dto.qr_request.GenerateQrCodeRequest;
import com.group_three.food_ordering.qr.qr_dto.qr_response.GenerateQrCodeResponse;
import com.group_three.food_ordering.repositories.DiningTableRepository;
import com.group_three.food_ordering.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.DINING_TABLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private final CloudinaryService cloudinaryService;
    private final DiningTableRepository tableRepository;
    private final TenantContext tenantContext;
    private final QrCodeGenerator qrCodeGenerator;
    private final QrCodeUrlBuilder urlBuilder;

    @Override
    @Transactional
    public GenerateQrCodeResponse generateTableQrCode(GenerateQrCodeRequest request) {
        log.info("[QrCodeService] Generating QR code for table number {} with baseUrl: {}",
                request.tableNumber(), request.baseUrl());

        // 1. Obtener venue actual
        FoodVenue venue = tenantContext.requireFoodVenue();

        // 2. Buscar mesa por número dentro del venue usando el publicId
        DiningTable table = tableRepository
                .findByFoodVenuePublicIdAndNumber(venue.getPublicId(), request.tableNumber())
                .orElseThrow(() -> new EntityNotFoundException(
                        DINING_TABLE,
                        "Table number " + request.tableNumber() + " not found in venue " + venue.getName()
                ));

        UUID tableId = table.getPublicId();

        // 3. Construir URL: simplemente baseUrl + tableId
        String tableUrl = urlBuilder.buildTableUrl(request.baseUrl(), tableId);
        log.debug("[QrCodeService] QR will redirect to: {}", tableUrl);

        // 4. Generar QR code
        String topLabel = String.format("Mesa %d", table.getNumber());
        String bottomLabel = venue.getName();
        byte[] qrCodeBytes = qrCodeGenerator.generateQrCodeWithLabels(tableUrl, topLabel, bottomLabel);

        // 5. Subir a Cloudinary
        String identifier = "table-" + tableId;
        String qrCodeUrl = cloudinaryService.uploadQrCode(qrCodeBytes, venue.getName(), identifier);

        // 6. Actualizar tabla
        tableRepository.updateQrCodeUrl(tableId, qrCodeUrl);

        log.info("[QrCodeService] QR code generated and saved: {}", qrCodeUrl);
        return new GenerateQrCodeResponse(qrCodeUrl);
    }
}
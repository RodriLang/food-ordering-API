package com.group_three.food_ordering.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group_three.food_ordering.enums.CloudinaryFolder;
import com.group_three.food_ordering.exceptions.CloudinaryException;
import com.group_three.food_ordering.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file, String venueName, CloudinaryFolder folder) {
        try {
            log.debug("[CloudinaryService] Uploading image to Cloudinary: {}", file.getOriginalFilename());
            String folderPath = String.join("/", "food_ordering", venueName, folder.getFolderName());

            return uploadToCloudinary(
                    file.getBytes(),
                    folderPath,
                    null, // sin public_id específico
                    false // no overwrite
            );
        } catch (IOException e) {
            log.error("[CloudinaryService] Error uploading image to Cloudinary", e);
            throw new CloudinaryException("Failed to upload image.", e);
        }
    }

    @Override
    public String uploadQrCode(byte[] qrCodeBytes, String venueName, String identifier) {
        try {
            log.debug("[CloudinaryService] Uploading QR code to Cloudinary for venue: {}", venueName);
            String folderPath = String.join("/", "food_ordering", venueName, "qr-codes");

            return uploadToCloudinary(
                    qrCodeBytes,
                    folderPath,
                    identifier,
                    true // overwrite
            );
        } catch (IOException e) {
            log.error("[CloudinaryService] Error uploading QR code to Cloudinary", e);
            throw new CloudinaryException("Failed to upload QR code.", e);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            log.debug("[CloudinaryService] Deleting image from Cloudinary: {}", publicId);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("[CloudinaryService] Image deleted successfully: {}", publicId);
        } catch (IOException e) {
            log.error("[CloudinaryService] Error deleting image from Cloudinary", e);
            throw new CloudinaryException("Failed to delete image.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private String uploadToCloudinary(byte[] fileBytes, String folderPath, String publicId, boolean overwrite)
            throws IOException {

        Map<String, Object> params = new HashMap<>();
        params.put("folder", folderPath);
        params.put("resource_type", "image");

        if (publicId != null) {
            params.put("public_id", publicId);
            params.put("format", "png");
        }

        if (overwrite) {
            params.put("overwrite", true);
        }

        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileBytes, params);
        String imageUrl = (String) uploadResult.get("secure_url");

        log.info("[CloudinaryService] Upload successful to {}: {}", folderPath, imageUrl);
        return imageUrl;
    }
}
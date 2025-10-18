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
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file, CloudinaryFolder folder) {
        try {
            log.debug("[CloudinaryService] Uploading image to Cloudinary: {}", file.getOriginalFilename());

            String folderPath = "food_ordering/" + folder.getFolderName();

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderPath,
                            "resource_type", "image"
                    ));

            String imageUrl = (String) uploadResult.get("secure_url");
            log.info("[CloudinaryService] Image uploaded successfully to {}: {}", folderPath, imageUrl);

            return imageUrl;

        } catch (IOException e) {
            log.error("[CloudinaryService] Error uploading image to Cloudinary", e);
            throw new CloudinaryException("Failed to upload image.", e);
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
}

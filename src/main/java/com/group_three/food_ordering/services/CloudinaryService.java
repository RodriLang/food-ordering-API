package com.group_three.food_ordering.services;

import com.group_three.food_ordering.enums.CloudinaryFolder;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadImage(MultipartFile file, String venueName, CloudinaryFolder folder);

    String uploadGlobalImage(MultipartFile file, CloudinaryFolder folder);

    String uploadQrCode(byte[] qrCodeBytes, String venueName, String identifier);

    void deleteImage(String publicId);
}
package com.group_three.food_ordering.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadImage(MultipartFile file);
    void deleteImage(String publicId);
}

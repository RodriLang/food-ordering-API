package com.group_three.food_ordering.services;

import com.group_three.food_ordering.enums.CloudinaryFolder;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadImage(MultipartFile file, CloudinaryFolder folder);
    void deleteImage(String publicId);
}

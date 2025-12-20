package com.ecr14.marketplace.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecr14.marketplace.dto.response.ImageUploadResponse;
import com.ecr14.marketplace.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public ImageUploadResponse uploadImage(MultipartFile file, String folder) {
        try {
            // Ensure folder path starts with ecr14-marketplace
            String folderPath = folder;
            if (!folder.startsWith("ecr14-marketplace/")) {
                folderPath = "ecr14-marketplace/" + folder;
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folderPath,
                            "resource_type", "auto"
                    ));

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            return new ImageUploadResponse(url, publicId);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload image: " + e.getMessage());
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new BadRequestException("Failed to delete image: " + e.getMessage());
        }
    }
}

package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.response.ImageUploadResponse;
import com.ecr14.marketplace.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "marketplace") String folder) {
        ImageUploadResponse response = cloudinaryService.uploadImage(file, folder);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteImage(@PathVariable String publicId) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.noContent().build();
    }
}

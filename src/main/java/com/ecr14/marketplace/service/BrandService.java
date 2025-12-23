package com.ecr14.marketplace.service;

import com.ecr14.marketplace.dto.request.BrandRequest;
import com.ecr14.marketplace.dto.response.BrandResponse;
import com.ecr14.marketplace.entity.Brand;
import com.ecr14.marketplace.entity.User;
import com.ecr14.marketplace.entity.UserRole;
import com.ecr14.marketplace.exception.ResourceNotFoundException;
import com.ecr14.marketplace.exception.UnauthorizedException;
import com.ecr14.marketplace.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ecr14.marketplace.dto.response.ImageUploadResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public BrandResponse getBrandById(String id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return BrandResponse.fromEntity(brand);
    }

    public BrandResponse getBrandByOwnerId(String ownerId) {
        Brand brand = brandRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found for owner: " + ownerId));
        return BrandResponse.fromEntity(brand);
    }

    @Transactional
    public BrandResponse createBrand(BrandRequest request, String userId, MultipartFile logoFile) {
        User user = userService.getUserById(userId);

        // Check if user already has a brand (for admin users)
        if (user.getRole() == UserRole.ADMIN && user.getBrandId() != null) {
            throw new UnauthorizedException("User already has a brand");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setPhone(request.getPhone());
        brand.setEmail(request.getEmail());
        brand.setCategories(request.getCategories());
        brand.setMinNoticeDays(request.getMinNoticeDays());
        brand.setOwnerId(userId);

        // Upload logo to Cloudinary if provided
        if (logoFile != null && !logoFile.isEmpty()) {
            // Create brand-specific folder: brandId will be generated after save, so use sanitized name
            String folderName = sanitizeFolderName(request.getName());
            ImageUploadResponse uploadResponse = cloudinaryService.uploadImage(logoFile, folderName);
            brand.setLogo(uploadResponse.getUrl());
        }

        brand = brandRepository.save(brand);

        // Update user's brandId if admin
        if (user.getRole() == UserRole.ADMIN) {
            user.setBrandId(brand.getId());
            userService.updateUser(user);
        }

        return BrandResponse.fromEntity(brand);
    }

    private String sanitizeFolderName(String name) {
        // Convert to lowercase, remove special characters except spaces and dashes
        // Replace Arabic/special chars and spaces with dashes
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    @Transactional
    public BrandResponse updateBrand(String id, BrandRequest request, String userId, MultipartFile logoFile) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        User user = userService.getUserById(userId);

        // Authorization check: Admin can only update their own brand, SuperAdmin can update any brand
        if (user.getRole() == UserRole.ADMIN && !brand.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this brand");
        }

        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setPhone(request.getPhone());
        brand.setEmail(request.getEmail());
        brand.setCategories(request.getCategories());
        brand.setMinNoticeDays(request.getMinNoticeDays());

        // Upload new logo to Cloudinary if provided
        if (logoFile != null && !logoFile.isEmpty()) {
            String folderName = sanitizeFolderName(request.getName());
            ImageUploadResponse uploadResponse = cloudinaryService.uploadImage(logoFile, folderName);
            brand.setLogo(uploadResponse.getUrl());
        }

        brand = brandRepository.save(brand);

        return BrandResponse.fromEntity(brand);
    }

    public void deleteBrand(String id, String userId) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        User user = userService.getUserById(userId);

        // Only SuperAdmin can delete brands via this method, or Admin can delete their own
        if (user.getRole() == UserRole.ADMIN && !brand.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this brand");
        }

        brandRepository.delete(brand);
    }
}

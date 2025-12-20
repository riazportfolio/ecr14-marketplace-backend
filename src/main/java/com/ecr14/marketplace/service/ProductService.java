package com.ecr14.marketplace.service;

import com.ecr14.marketplace.dto.request.ProductRequest;
import com.ecr14.marketplace.dto.response.ProductResponse;
import com.ecr14.marketplace.entity.Brand;
import com.ecr14.marketplace.entity.Product;
import com.ecr14.marketplace.entity.User;
import com.ecr14.marketplace.entity.UserRole;
import com.ecr14.marketplace.exception.ResourceNotFoundException;
import com.ecr14.marketplace.exception.UnauthorizedException;
import com.ecr14.marketplace.repository.BrandRepository;
import com.ecr14.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ecr14.marketplace.dto.response.ImageUploadResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public List<ProductResponse> getProductsByBrandId(String brandId) {
        return productRepository.findByBrandId(brandId).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request, String userId, MultipartFile imageFile) {
        User user = userService.getUserById(userId);

        // For admin users, automatically use their brand
        final String finalBrandId;
        if (user.getRole() == UserRole.ADMIN && user.getBrandId() != null) {
            finalBrandId = user.getBrandId();
        } else {
            finalBrandId = request.getBrandId();
        }

        Brand brand = brandRepository.findById(finalBrandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + finalBrandId));

        // Authorization: Admin can only create products for their own brand
        if (user.getRole() == UserRole.ADMIN && !brand.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to create products for this brand");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setOffer(request.getOffer());
        product.setBrandId(brand.getId());
        product.setBrandName(brand.getName());
        product.setBrandPhone(brand.getPhone());

        // Upload image to Cloudinary if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String folderName = sanitizeFolderName(brand.getName());
            ImageUploadResponse uploadResponse = cloudinaryService.uploadImage(imageFile, folderName);
            product.setImage(uploadResponse.getUrl());
        }

        product = productRepository.save(product);

        return ProductResponse.fromEntity(product);
    }

    private String sanitizeFolderName(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    public ProductResponse updateProduct(String id, ProductRequest request, String userId, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User user = userService.getUserById(userId);
        Brand brand = brandRepository.findById(product.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        // Authorization: Admin can only update products from their own brand
        if (user.getRole() == UserRole.ADMIN && !brand.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this product");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setOffer(request.getOffer());

        // Upload new image to Cloudinary if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String folderName = sanitizeFolderName(brand.getName());
            ImageUploadResponse uploadResponse = cloudinaryService.uploadImage(imageFile, folderName);
            product.setImage(uploadResponse.getUrl());
        }

        // If brand changed, update denormalized fields
        if (request.getBrandId() != null && !product.getBrandId().equals(request.getBrandId())) {
            Brand newBrand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));

            // Check authorization for new brand
            if (user.getRole() == UserRole.ADMIN && !newBrand.getOwnerId().equals(userId)) {
                throw new UnauthorizedException("You are not authorized to assign products to this brand");
            }

            product.setBrandId(newBrand.getId());
            product.setBrandName(newBrand.getName());
            product.setBrandPhone(newBrand.getPhone());
        }

        product = productRepository.save(product);

        return ProductResponse.fromEntity(product);
    }

    public void deleteProduct(String id, String userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User user = userService.getUserById(userId);
        Brand brand = brandRepository.findById(product.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        // Authorization: Admin can only delete products from their own brand
        if (user.getRole() == UserRole.ADMIN && !brand.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this product");
        }

        productRepository.delete(product);
    }
}

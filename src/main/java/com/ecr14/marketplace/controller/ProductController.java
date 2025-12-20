package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.request.ProductRequest;
import com.ecr14.marketplace.dto.response.ProductResponse;
import com.ecr14.marketplace.service.ProductService;
import com.ecr14.marketplace.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<ProductResponse>> getProductsByBrandId(@PathVariable String brandId) {
        List<ProductResponse> products = productService.getProductsByBrandId(brandId);
        return ResponseEntity.ok(products);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("unit") String unit,
            @RequestParam(value = "offer", required = false) String offer,
            @RequestParam(value = "brandId", required = false) String brandId,
            @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile imageFile) {

        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setCategory(category);
        request.setPrice(price);
        request.setUnit(unit);
        request.setOffer(offer);
        request.setBrandId(brandId);

        String userId = securityUtils.getCurrentUserId();
        ProductResponse product = productService.createProduct(request, userId, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("unit") String unit,
            @RequestParam(value = "offer", required = false) String offer,
            @RequestParam(value = "brandId", required = false) String brandId,
            @RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile imageFile) {

        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setCategory(category);
        request.setPrice(price);
        request.setUnit(unit);
        request.setOffer(offer);
        request.setBrandId(brandId);

        String userId = securityUtils.getCurrentUserId();
        ProductResponse product = productService.updateProduct(id, request, userId, imageFile);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        String userId = securityUtils.getCurrentUserId();
        productService.deleteProduct(id, userId);
        return ResponseEntity.noContent().build();
    }
}

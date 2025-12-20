package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.request.BrandRequest;
import com.ecr14.marketplace.dto.response.BrandResponse;
import com.ecr14.marketplace.service.BrandService;
import com.ecr14.marketplace.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable String id) {
        BrandResponse brand = brandService.getBrandById(id);
        return ResponseEntity.ok(brand);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<BrandResponse> getBrandByOwnerId(@PathVariable String ownerId) {
        BrandResponse brand = brandService.getBrandByOwnerId(ownerId);
        return ResponseEntity.ok(brand);
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<BrandResponse> createBrand(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("categories") List<String> categories,
            @RequestParam(value = "logo", required = false) org.springframework.web.multipart.MultipartFile logoFile) {

        BrandRequest request = new BrandRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPhone(phone);
        request.setEmail(email);
        request.setCategories(categories);

        String userId = securityUtils.getCurrentUserId();
        BrandResponse brand = brandService.createBrand(request, userId, logoFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(brand);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("categories") List<String> categories,
            @RequestParam(value = "logo", required = false) org.springframework.web.multipart.MultipartFile logoFile) {

        BrandRequest request = new BrandRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPhone(phone);
        request.setEmail(email);
        request.setCategories(categories);

        String userId = securityUtils.getCurrentUserId();
        BrandResponse brand = brandService.updateBrand(id, request, userId, logoFile);
        return ResponseEntity.ok(brand);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        String userId = securityUtils.getCurrentUserId();
        brandService.deleteBrand(id, userId);
        return ResponseEntity.noContent().build();
    }
}

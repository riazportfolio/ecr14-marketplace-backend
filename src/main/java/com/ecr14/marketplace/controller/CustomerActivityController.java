package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.response.ProductResponse;
import com.ecr14.marketplace.service.CustomerActivityService;
import com.ecr14.marketplace.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer-activity")
public class CustomerActivityController {

    @Autowired
    private CustomerActivityService customerActivityService;

    @Autowired
    private SecurityUtils securityUtils;

    // Track product view
    @PostMapping("/track-view/{productId}")
    public ResponseEntity<Void> trackProductView(@PathVariable String productId) {
        String userId = securityUtils.getCurrentUserId();
        customerActivityService.trackProductView(userId, productId);
        return ResponseEntity.ok().build();
    }

    // Track search
    @PostMapping("/track-search")
    public ResponseEntity<Void> trackSearch(@RequestBody Map<String, String> request) {
        String userId = securityUtils.getCurrentUserId();
        String searchQuery = request.get("query");
        customerActivityService.trackSearch(userId, searchQuery);
        return ResponseEntity.ok().build();
    }

    // Select/favorite a product (intent to order)
    @PostMapping("/select/{productId}")
    public ResponseEntity<Void> selectProduct(@PathVariable String productId) {
        String userId = securityUtils.getCurrentUserId();
        customerActivityService.selectProduct(userId, productId);
        return ResponseEntity.ok().build();
    }

    // Unselect/remove from favorites
    @DeleteMapping("/select/{productId}")
    public ResponseEntity<Void> unselectProduct(@PathVariable String productId) {
        String userId = securityUtils.getCurrentUserId();
        customerActivityService.unselectProduct(userId, productId);
        return ResponseEntity.ok().build();
    }

    // Get customer's personalized recommendations
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations() {
        String userId = securityUtils.getCurrentUserId();

        List<ProductResponse> selectedProducts = customerActivityService.getSelectedProducts(userId);
        List<ProductResponse> searchBasedRecommendations = customerActivityService.getRecommendedProductsFromSearches(userId, 10);
        List<ProductResponse> viewBasedRecommendations = customerActivityService.getRecommendedProductsFromViews(userId, 10);
        List<String> recentSearches = customerActivityService.getRecentSearches(userId, 5);

        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("selectedProducts", selectedProducts);
        recommendations.put("searchBasedRecommendations", searchBasedRecommendations);
        recommendations.put("viewBasedRecommendations", viewBasedRecommendations);
        recommendations.put("recentSearches", recentSearches);

        return ResponseEntity.ok(recommendations);
    }

    // Get selected products only (for reorder section)
    @GetMapping("/selected-products")
    public ResponseEntity<List<ProductResponse>> getSelectedProducts() {
        String userId = securityUtils.getCurrentUserId();
        List<ProductResponse> products = customerActivityService.getSelectedProducts(userId);
        return ResponseEntity.ok(products);
    }
}

package com.ecr14.marketplace.service;

import com.ecr14.marketplace.dto.response.ProductResponse;
import com.ecr14.marketplace.entity.CustomerProductView;
import com.ecr14.marketplace.entity.CustomerSearchHistory;
import com.ecr14.marketplace.entity.CustomerSelectedProduct;
import com.ecr14.marketplace.entity.Product;
import com.ecr14.marketplace.repository.CustomerProductViewRepository;
import com.ecr14.marketplace.repository.CustomerSearchHistoryRepository;
import com.ecr14.marketplace.repository.CustomerSelectedProductRepository;
import com.ecr14.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerActivityService {

    @Autowired
    private CustomerProductViewRepository productViewRepository;

    @Autowired
    private CustomerSearchHistoryRepository searchHistoryRepository;

    @Autowired
    private CustomerSelectedProductRepository selectedProductRepository;

    @Autowired
    private ProductRepository productRepository;

    // Track product view
    @Transactional
    public void trackProductView(String userId, String productId) {
        CustomerProductView view = new CustomerProductView();
        view.setUserId(userId);
        view.setProductId(productId);
        productViewRepository.save(view);
    }

    // Track search
    @Transactional
    public void trackSearch(String userId, String searchQuery) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            CustomerSearchHistory search = new CustomerSearchHistory();
            search.setUserId(userId);
            search.setSearchQuery(searchQuery.trim().toLowerCase());
            searchHistoryRepository.save(search);
        }
    }

    // Add product to selected/favorites
    @Transactional
    public void selectProduct(String userId, String productId) {
        Optional<CustomerSelectedProduct> existing = selectedProductRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            // Update timestamp
            CustomerSelectedProduct selected = existing.get();
            selected.setLastUpdated(LocalDateTime.now());
            selectedProductRepository.save(selected);
        } else {
            CustomerSelectedProduct selected = new CustomerSelectedProduct();
            selected.setUserId(userId);
            selected.setProductId(productId);
            selectedProductRepository.save(selected);
        }
    }

    // Remove product from selected
    @Transactional
    public void unselectProduct(String userId, String productId) {
        selectedProductRepository.deleteByUserIdAndProductId(userId, productId);
    }

    // Get customer's selected products for reorder
    public List<ProductResponse> getSelectedProducts(String userId) {
        List<String> productIds = selectedProductRepository.findProductIdsByUserIdOrderByLastUpdatedDesc(userId);
        List<Product> products = productRepository.findAllById(productIds);

        // Maintain order from selected products
        Map<String, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, p -> p));

        return productIds.stream()
            .map(productMap::get)
            .filter(Objects::nonNull)
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // Get recommended products based on search history
    public List<ProductResponse> getRecommendedProductsFromSearches(String userId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30); // Last 30 days
        List<String> recentSearches = searchHistoryRepository.findDistinctRecentSearches(userId, since);

        if (recentSearches.isEmpty()) {
            return Collections.emptyList();
        }

        // Get most recent search term
        String mostRecentSearch = recentSearches.get(0);

        // Find products matching the search term
        List<Product> matchingProducts = productRepository.findAll().stream()
            .filter(p ->
                (p.getName() != null && p.getName().toLowerCase().contains(mostRecentSearch)) ||
                (p.getCategory() != null && p.getCategory().toLowerCase().contains(mostRecentSearch)) ||
                (p.getDescription() != null && p.getDescription().toLowerCase().contains(mostRecentSearch))
            )
            .limit(limit)
            .collect(Collectors.toList());

        return matchingProducts.stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // Get products from categories customer has viewed
    public List<ProductResponse> getRecommendedProductsFromViews(String userId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30); // Last 30 days
        List<String> viewedProductIds = productViewRepository.findDistinctProductIdsByUserIdOrderByViewedAtDesc(userId);

        if (viewedProductIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Get categories from viewed products
        List<Product> viewedProducts = productRepository.findAllById(viewedProductIds);
        Set<String> viewedCategories = viewedProducts.stream()
            .map(Product::getCategory)
            .collect(Collectors.toSet());

        // Find other products in same categories (excluding already viewed)
        List<Product> recommendations = productRepository.findAll().stream()
            .filter(p -> viewedCategories.contains(p.getCategory()))
            .filter(p -> !viewedProductIds.contains(p.getId()))
            .limit(limit)
            .collect(Collectors.toList());

        return recommendations.stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // Get customer's recent search terms
    public List<String> getRecentSearches(String userId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<String> searches = searchHistoryRepository.findDistinctRecentSearches(userId, since);
        return searches.stream().limit(limit).collect(Collectors.toList());
    }
}

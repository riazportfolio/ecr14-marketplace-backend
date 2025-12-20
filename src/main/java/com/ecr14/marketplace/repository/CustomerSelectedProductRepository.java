package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.CustomerSelectedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerSelectedProductRepository extends JpaRepository<CustomerSelectedProduct, String> {

    List<CustomerSelectedProduct> findByUserIdOrderByLastUpdatedDesc(String userId);

    Optional<CustomerSelectedProduct> findByUserIdAndProductId(String userId, String productId);

    @Query("SELECT csp.productId FROM CustomerSelectedProduct csp WHERE csp.userId = :userId ORDER BY csp.lastUpdated DESC")
    List<String> findProductIdsByUserIdOrderByLastUpdatedDesc(@Param("userId") String userId);

    void deleteByUserIdAndProductId(String userId, String productId);
}

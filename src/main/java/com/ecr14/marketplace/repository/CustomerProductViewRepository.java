package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.CustomerProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerProductViewRepository extends JpaRepository<CustomerProductView, String> {

    List<CustomerProductView> findByUserIdOrderByViewedAtDesc(String userId);

    @Query("SELECT cpv FROM CustomerProductView cpv WHERE cpv.userId = :userId AND cpv.viewedAt >= :since ORDER BY cpv.viewedAt DESC")
    List<CustomerProductView> findRecentViewsByUser(@Param("userId") String userId, @Param("since") LocalDateTime since);

    @Query("SELECT cpv.productId FROM CustomerProductView cpv WHERE cpv.userId = :userId GROUP BY cpv.productId ORDER BY MAX(cpv.viewedAt) DESC")
    List<String> findDistinctProductIdsByUserIdOrderByViewedAtDesc(@Param("userId") String userId);
}

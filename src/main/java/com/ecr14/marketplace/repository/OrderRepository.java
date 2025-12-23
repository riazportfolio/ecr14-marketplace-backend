package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.Order;
import com.ecr14.marketplace.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Order> findByBrandIdOrderByCreatedAtDesc(String brandId);

    List<Order> findByBrandIdAndStatusOrderByCreatedAtDesc(String brandId, OrderStatus status);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}

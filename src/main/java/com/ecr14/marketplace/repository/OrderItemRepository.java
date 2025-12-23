package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByOrderIdOrderById(String orderId);

    void deleteByOrderId(String orderId);
}

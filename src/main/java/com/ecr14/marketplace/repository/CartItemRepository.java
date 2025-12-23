package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCartIdOrderByAddedAtAsc(String cartId);

    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);

    void deleteByCartId(String cartId);

    Long countByCartId(String cartId);
}

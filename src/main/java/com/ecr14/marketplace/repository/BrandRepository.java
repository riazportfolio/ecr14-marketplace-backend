package com.ecr14.marketplace.repository;

import com.ecr14.marketplace.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    Optional<Brand> findByOwnerId(String ownerId);
}

package com.thurman.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository
        extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.isPublished AND p.stockLevel > 0 ORDER BY p.price ASC")
    List<Product> findAvailablePublishedProducts();
}

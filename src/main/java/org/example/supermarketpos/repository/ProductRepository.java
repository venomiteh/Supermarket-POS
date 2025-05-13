package org.example.supermarketpos.repository;

import org.example.supermarketpos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Add custom query methods here if needed
}

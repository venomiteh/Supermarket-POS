package org.example.supermarketpos.repository;

import org.example.supermarketpos.model.Inventory;
import org.example.supermarketpos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // You can define custom queries for stock management
    Optional<Inventory> findByProduct(Product product);
    @Query("SELECT i FROM Inventory i WHERE i.quantityInStock > 0")
    List<Inventory> findAvailableProducts();



}

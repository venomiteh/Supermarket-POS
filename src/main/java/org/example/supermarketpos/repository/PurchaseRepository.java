package org.example.supermarketpos.repository;

import org.example.supermarketpos.model.Purchase;
import org.example.supermarketpos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // Custom queries for Purchase-related data
    List<Purchase> findByUserId(Long userId);
    List<Purchase> findByUser(User user);
}

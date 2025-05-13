package org.example.supermarketpos.repository;

import org.example.supermarketpos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom queries like finding a user by username, etc.
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}

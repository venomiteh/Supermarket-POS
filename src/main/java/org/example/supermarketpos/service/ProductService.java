package org.example.supermarketpos.service;

import org.example.supermarketpos.model.Inventory;
import org.example.supermarketpos.model.Product;
import org.example.supermarketpos.repository.InventoryRepository;
import org.example.supermarketpos.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // Add a new product + set its inventory stock
    public Product addProduct(Product product, int initialStock) {
        Product savedProduct = productRepository.save(product);
        Inventory inventory = new Inventory(savedProduct, initialStock);
        inventoryRepository.save(inventory);
        return savedProduct;
    }

    // Edit product info (name, price, cost) + update stock if needed
    public Product updateProduct(Long productId, Product updatedProduct, Integer updatedStock) {
        return productRepository.findById(productId).map(product -> {
            product.setName(updatedProduct.getName());
            product.setSellingPrice(updatedProduct.getSellingPrice());
            product.setCost(updatedProduct.getCost());
            Product saved = productRepository.save(product);

            // If stock is passed, update it too
            if (updatedStock != null) {
                Inventory inventory = inventoryRepository.findByProduct(product)
                        .orElseThrow(() -> new RuntimeException("Inventory not found for product."));
                inventory.setQuantityInStock(updatedStock);
                inventoryRepository.save(inventory);
            }

            return saved;
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    //  Soft Delete product + its inventory
    public void deleteProduct(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setActive(false); // Mark as inactive instead of deleting
            productRepository.save(product);
        });
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Helper to get stock of a product
    public Optional<Inventory> getInventoryByProduct(Product product) {
        return inventoryRepository.findByProduct(product);
    }
}

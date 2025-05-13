package org.example.supermarketpos.service;

import org.example.supermarketpos.model.Inventory;
import org.example.supermarketpos.model.Product;
import org.example.supermarketpos.repository.InventoryRepository;
import org.example.supermarketpos.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;



    public InventoryService(InventoryRepository inventoryRepository,ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public Inventory addInventory(Product product,int quantity) {
        Inventory inventory = new Inventory(product,quantity);
        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(long id,int quantity) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        inventory.setQuantityInStock(quantity);
        return inventoryRepository.save(inventory);
        }

    public List<Inventory> getInventory() {
        return inventoryRepository.findAll()
                .stream()
                .filter(inv -> inv.getProduct().isActive())
                .toList();
    }

    public Inventory getInventoryById(long id) {
        return inventoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    public void deleteInventoryById(long id) {
        inventoryRepository.deleteById(id);
    }

    public List<Inventory> getAvailableItems() {
        return inventoryRepository.findAvailableProducts().stream()
                .filter(inv -> inv.getProduct().isActive())
                .toList();
    }


    public  List<Inventory> findAllInventory() {
        return inventoryRepository.findAll();
    }


    public List<Inventory> getOutOfStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getQuantityInStock() == 0)
                .toList();
    }

    public List<Inventory> getLowStockItems(int threshold) {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getQuantityInStock() > 0 && inv.getQuantityInStock() <= threshold)
                .toList();
    }


}


package org.example.supermarketpos.service;

import org.example.supermarketpos.model.*;
import org.example.supermarketpos.repository.InventoryRepository;
import org.example.supermarketpos.repository.ProductRepository;
import org.example.supermarketpos.repository.PurchaseRepository;
import org.example.supermarketpos.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository
    ) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Purchase> getPurchasesByUser(User user) {
        return purchaseRepository.findByUser(user);
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public void makeCartPurchase(String username, List<Map<String, Object>> cart) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PurchaseItem> items = new ArrayList<>();
        double total = 0.0;

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setPurchaseDate(LocalDate.now().toString());

        for (Map<String, Object> entry : cart) {
            Long productId = Long.parseLong(entry.get("itemId").toString());
            int quantity = Integer.parseInt(entry.get("quantity").toString());

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (!product.isActive()) {
                throw new IllegalStateException("This product is no longer available.");
            }

            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product"));

            if (inventory.getQuantityInStock() < quantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            inventory.setQuantityInStock(inventory.getQuantityInStock() - quantity);
            inventoryRepository.save(inventory);

            PurchaseItem item = new PurchaseItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPurchase(purchase);

            items.add(item);
            total += product.getSellingPrice() * quantity;
        }

        purchase.setItems(items);
        purchase.setTotalPrice(total);

        purchaseRepository.save(purchase);
    }

    public Map<String, Integer> getMonthlyPurchaseCounts() {
        List<Purchase> purchases = purchaseRepository.findAll();
        Map<String, Integer> purchaseCount = new HashMap<>();

        for (Purchase p : purchases) {
            String month = LocalDate.parse(p.getPurchaseDate())
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            purchaseCount.put(month, purchaseCount.getOrDefault(month, 0) + 1);
        }
        return purchaseCount;
    }

    public Map<String, Double> getMonthlyProfits() {
        List<Purchase> purchases = purchaseRepository.findAll();
        Map<String, Double> monthlyProfits = new HashMap<>();

        for (Purchase p : purchases) {
            String month = LocalDate.parse(p.getPurchaseDate())
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            double profit = 0;
            for (PurchaseItem item : p.getItems()) {
                double itemProfit = (item.getProduct().getSellingPrice() - item.getProduct().getCost()) * item.getQuantity();
                profit += itemProfit;
            }

            monthlyProfits.put(month, monthlyProfits.getOrDefault(month, 0.0) + profit);
        }
        return monthlyProfits;
    }

    public List<Purchase> getPurchasesByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Collections.emptyList(); // or throw new RuntimeException
        }
        return purchaseRepository.findByUser(userOpt.get());
    }
}

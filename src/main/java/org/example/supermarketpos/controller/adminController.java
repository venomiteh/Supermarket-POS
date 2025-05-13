package org.example.supermarketpos.controller;

import org.example.supermarketpos.model.Inventory;
import org.example.supermarketpos.model.Product;
import org.example.supermarketpos.model.Purchase;
import org.example.supermarketpos.model.User;
import org.example.supermarketpos.service.InventoryService;
import org.example.supermarketpos.service.ProductService;
import org.example.supermarketpos.service.PurchaseService;
import org.example.supermarketpos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class adminController {

    private final InventoryService inventoryService;
    private final ProductService productService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    @Autowired
    public adminController(InventoryService inventoryService, ProductService productService, UserService userService,PurchaseService purchaseService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.userService = userService;
        this.purchaseService = purchaseService;
    }

    @RequestMapping("/dashboard")
    public String dashboard( Model model) {
        model.addAttribute("inventoryList", inventoryService.getInventory());
        model.addAttribute("PurchaseLogs", inventoryService.findAllInventory());
        model.addAttribute("outOfStock", inventoryService.getOutOfStockItems());
        model.addAttribute("lowStock", inventoryService.getLowStockItems(5)); // threshold = 5

        // Statistics Calculation
        List<Purchase> purchases = purchaseService.getAllPurchases();
        Map<String, Integer> monthlyPurchases = new HashMap<>();
        Map<String, Double> monthlyProfits = new HashMap<>();

        for (Purchase p : purchases) {
            LocalDate date = LocalDate.parse(p.getPurchaseDate());
            String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            // Count purchases
            monthlyPurchases.put(month, monthlyPurchases.getOrDefault(month, 0) + 1);

            // Calculate profit
            double profit = p.getItems().stream()
                    .mapToDouble(item -> (item.getProduct().getSellingPrice() - item.getProduct().getCost()) * item.getQuantity())
                    .sum();
            monthlyProfits.put(month, monthlyProfits.getOrDefault(month, 0.0) + profit);
        }

        model.addAttribute("monthlyPurchases", monthlyPurchases);
        model.addAttribute("monthlyProfits", monthlyProfits);


        return "admin-dashboard";
    }

    @PostMapping("/register-user")
    @ResponseBody  // Make sure it returns raw response, not view name
    public ResponseEntity<Map<String, String>> registerUser(@ModelAttribute User user) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.registerUser(user);
            response.put("success", "true");
            response.put("message", "User registered successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", "false");
            response.put("error", "Error registering user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Inside adminController.java
    @PostMapping("/add-product")
    public String addProductToInventory(@RequestParam String name,
                                        @RequestParam double cost,
                                        @RequestParam double sellingPrice,
                                        @RequestParam int quantity,
                                        Model model) {
        // Create a new Product
        Product product = new Product();
        product.setName(name);
        product.setCost(cost);
        product.setSellingPrice(sellingPrice);

        // Save product to the product repository and add it to inventory
        Product savedProduct = productService.addProduct(product, quantity);

        // Optionally, you can display a success message or redirect the user to the inventory page
        model.addAttribute("inventoryList", inventoryService.getInventory());
        return "redirect:/admin/dashboard";  // Redirect to the dashboard
    }

    @PostMapping("/update-product")
    public String updateProduct(@RequestParam Long id,
                                @RequestParam Long productId,
                                @RequestParam String name,
                                @RequestParam double cost,
                                @RequestParam double sellingPrice,
                                @RequestParam int quantity,
                                Model model) {

        Product updatedProduct = new Product();
        updatedProduct.setName(name);
        updatedProduct.setCost(cost);
        updatedProduct.setSellingPrice(sellingPrice);

        productService.updateProduct(productId, updatedProduct, quantity);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Inventory inventory = inventoryService.getInventoryById(id);
        Product product = inventory.getProduct();

        // First delete inventory, then product
        inventoryService.deleteInventoryById(id);
        productService.deleteProduct(product.getId());

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/ajax/search-purchases")
    @ResponseBody
    public ResponseEntity<?> searchUserPurchasesAjax(@RequestParam("username") String username) {
        try {
            List<Purchase> userPurchases = purchaseService.getPurchasesByUsername(username.trim());
            if (userPurchases.isEmpty()) {
                return ResponseEntity.ok(Map.of("success", false, "message", "No purchases found for user: " + username));
            }
            return ResponseEntity.ok(Map.of("success", true, "purchases", userPurchases));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPassword(newPassword);  // Optionally hash this if you implement encoding
            userService.save(user);

            response.put("success", "true");
            response.put("message", "Password updated successfully for user: " + username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", "false");
            response.put("error", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

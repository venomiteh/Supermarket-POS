package org.example.supermarketpos.controller;


import org.example.supermarketpos.model.Purchase;
import org.example.supermarketpos.model.User;
import org.example.supermarketpos.service.InventoryService;
import org.example.supermarketpos.service.PurchaseService;
import org.example.supermarketpos.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class userController {
    private final InventoryService inventoryService;
    private final UserService userService;
    private final PurchaseService purchaseService;


    public userController(InventoryService inventoryService, UserService userService, PurchaseService purchaseService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
      this.purchaseService = purchaseService;
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        model.addAttribute("items", inventoryService.getAvailableItems());

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        List<Purchase> purchases = purchaseService.getPurchasesByUser(user);

        model.addAttribute("purchases", purchases);
        return "userDashboard";  // Your Thymeleaf HTML
    }
    @PostMapping("/purchase-cart")
    @ResponseBody
    public ResponseEntity<String> purchaseCart(@RequestBody List<Map<String, Object>> cart,
                                               Authentication auth) {
        try {
            String username = auth.getName();
            purchaseService.makeCartPurchase(username, cart);
            return ResponseEntity.ok("Purchase successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}

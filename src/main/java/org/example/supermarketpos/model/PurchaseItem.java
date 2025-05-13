package org.example.supermarketpos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private int quantity;

    @JsonBackReference
    @ManyToOne
    private Purchase purchase;

    public PurchaseItem() {}

    public PurchaseItem(Product product, int quantity, Purchase purchase) {
        this.product = product;
        this.quantity = quantity;
        this.purchase = purchase;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Purchase getPurchase() { return purchase; }

    public void setPurchase(Purchase purchase) { this.purchase = purchase; }
}

package org.example.supermarketpos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

//The Purchase entity represents a transaction or sale where a customer buys products from the store. It links the purchased items to the buyer and the amount spent.
//
//Purpose:
//To record every purchase made by users.
//
//To keep track of which user made the purchase, the products involved, and the total price.
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    private String purchaseDate;

    private double totalPrice;

    @JsonManagedReference
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PurchaseItem> items;

    public Purchase() {}

    public Purchase(User user, String purchaseDate, double totalPrice, List<PurchaseItem> items) {
        this.user = user;
        this.purchaseDate = purchaseDate;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) { this.items = items; }
}


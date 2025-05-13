package org.example.supermarketpos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//
//The Product entity represents the items being sold in the store. It contains the product details such as name, price, quantity in stock, and more.
//
//        Purpose:
//To store and manage the products available for sale.
//
//To track the price, cost, and quantity of each product.
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//this is used to generate id's
    private Long id;
    private String name;
    private Double sellingPrice;//selling price
    private double cost;//Cost price
    private boolean active = true; // New field for soft delete


    public Product() {}
    public Product(Long id, String name, String description, Double price, double cost, int quantity) {
        this.id = id;
        this.name = name;
        this.sellingPrice = price;
        this.cost = cost;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }
    public void setSellingPrice(Double price) {
        this.sellingPrice = price;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }


}

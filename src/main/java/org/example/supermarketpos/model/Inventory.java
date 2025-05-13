package org.example.supermarketpos.model;

import jakarta.persistence.*;


//The Inventory entity tracks the stock of each product. It helps to manage the stock levels in the store.
//
//Purpose:
//To monitor the current stock levels of each product.
//
//To facilitate adding or removing products from the inventory.
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    //this refers to the product liked to the inventory
    @OneToOne
    private Product product;

    private int quantityInStock;

    public Inventory() {}
public Inventory(Product product, int quantityInStock) {
        this.product = product;
        this.quantityInStock = quantityInStock;

}
public long getId() {
        return id;
}
public void setId(long id) {
        this.id = id;
}
public Product getProduct() {
        return product;
}
public void setProduct(Product product) {
        this.product = product;
}
public int getQuantityInStock() {
        return quantityInStock;
}
public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
}

}

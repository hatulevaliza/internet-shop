package com.shop.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }  // ← ЭТОТ МЕТОД!

    public void reduceStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalArgumentException("Недостаточно товара");
        }
        stock -= quantity;
    }

    public boolean hasEnoughStock(int quantity) {
        return stock >= quantity;
    }

    @Override
    public String toString() {
        return name + " (остаток: " + stock + ", цена: " + price + ")";
    }
}
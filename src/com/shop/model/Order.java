package com.shop.model;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private int id;
    private Customer customer;
    private Map<Product, Integer> items;
    private double total;
    private String status;
    private Payment payment;

    public enum OrderStatus {
        CREATED, PAID, SHIPPED, DELIVERED, CANCELLED
    }

    public Order(int id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new HashMap<>();
        this.total = 0;
        this.status = "CREATED";
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Map<Product, Integer> getItems() { return items; }
    public double getTotal() { return total; }  // ← ЭТОТ МЕТОД!
    public String getStatus() { return status; }
    public Payment getPayment() { return payment; }

    public void addItem(Product product, int quantity) {
        if (!product.hasEnoughStock(quantity)) {
            throw new IllegalArgumentException("Нет товара: " + product.getName());
        }
        items.put(product, quantity);
        total += product.getPrice() * quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status.name();
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        this.status = OrderStatus.PAID.name();
    }

    public boolean isPaid() {
        return payment != null && payment.isSuccessful();
    }

    @Override
    public String toString() {
        return "Заказ #" + id + " | " + customer.getName() + " | сумма: " + total + " | " + status;
    }
}
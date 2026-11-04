package shop.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int id;
    private String name;
    private String email;
    private List<Order> orders;

    public Customer(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.orders = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Order> getOrders() { return orders; }

    public void addOrder(Order order) {
        orders.add(order);
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
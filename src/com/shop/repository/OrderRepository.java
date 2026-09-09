package com.shop.repository;

import com.shop.model.Order;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(int id);
    List<Order> findAll();  // ← ДОБАВЬТЕ ЭТОТ МЕТОД!
    void update(Order order);
}
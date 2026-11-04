package shop.repository;

import shop.model.Order;
import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(int id);
    List<Order> findAll();
    void update(Order order);
}
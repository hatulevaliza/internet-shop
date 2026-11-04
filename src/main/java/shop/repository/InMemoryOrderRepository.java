package shop.repository;

import shop.model.Order;

import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private Map<Integer, Order> storage = new HashMap<>();

    @Override
    public void save(Order order) {
        storage.put(order.getId(), order);
    }

    @Override
    public Order findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Order order) {
        storage.put(order.getId(), order);
    }
}
package shop.repository;

import shop.model.Product;
import java.util.*;

public class InMemoryProductRepository implements ProductRepository {
    private Map<Integer, Product> storage = new HashMap<>();

    @Override
    public void save(Product product) {
        storage.put(product.getId(), product);
    }

    @Override
    public Product findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Product product) {
        storage.put(product.getId(), product);
    }
}
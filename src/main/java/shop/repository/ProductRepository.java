package shop.repository;

import shop.model.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product product);
    Product findById(int id);
    List<Product> findAll();
    void update(Product product);
}
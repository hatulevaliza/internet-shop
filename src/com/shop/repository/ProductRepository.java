package com.shop.repository;

import com.shop.model.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product product);
    Product findById(int id);
    List<Product> findAll();  // ← ДОБАВЬТЕ ЭТОТ МЕТОД!
    void update(Product product);
}
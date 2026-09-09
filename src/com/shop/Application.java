package com.shop;

import com.shop.model.*;
import com.shop.repository.*;
import com.shop.service.OrderService;
import com.shop.service.NotificationService;

public class Application {
    public static void main(String[] args) {
        System.out.println("🛒 ИНТЕРНЕТ-МАГАЗИН");
        System.out.println("=".repeat(40));

        // Создание зависимостей
        ProductRepository productRepo = new InMemoryProductRepository();
        OrderRepository orderRepo = new InMemoryOrderRepository();
        NotificationService notificationService = new NotificationService();
        OrderService service = new OrderService(orderRepo, productRepo, notificationService);

        // Товары
        Product laptop = new Product(1, "Ноутбук", 1000, 5);
        Product phone = new Product(2, "Смартфон", 700, 3);
        Product headphones = new Product(3, "Наушники", 150, 1);

        productRepo.save(laptop);
        productRepo.save(phone);
        productRepo.save(headphones);

        System.out.println("\nТовары в магазине:");
        for (Product product : productRepo.findAll()) {
            System.out.println("   " + product);
        }

        Customer customer = new Customer(1, "Иван Петров", "ivan@example.com");
        System.out.println("\n👤 Покупатель: " + customer);

        System.out.println("\n" + "-".repeat(40));
        System.out.println("ОФОРМЛЕНИЕ ЗАКАЗА");
        System.out.println("-".repeat(40));

        try {
            System.out.println("\nПопытка купить ноутбук (1 шт.):");
            Order order1 = service.createOrder(customer, laptop, 1);
            System.out.println("   " + order1);
        } catch (Exception e) {
            System.out.println("   Ошибка: " + e.getMessage());
        }

        try {
            System.out.println("\nПопытка купить наушники (10 шт. — есть 1):");
            Order order2 = service.createOrder(customer, headphones, 10);
            System.out.println("   " + order2);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        try {
            System.out.println("\nПопытка купить 10 смартфонов (есть 3):");
            Order order3 = service.createOrder(customer, phone, 10);
            System.out.println("   " + order3);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\n" + "=".repeat(40));
        System.out.println("ИТОГИ:");
        System.out.println("\nОстатки товаров:");
        for (Product product : productRepo.findAll()) {
            System.out.println("   " + product);
        }
        System.out.println("\nЗаказы покупателя:");
        for (Order order : customer.getOrders()) {
            System.out.println("   " + order);
        }
    }
}
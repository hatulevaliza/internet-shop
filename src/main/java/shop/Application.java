package shop;

import shop.model.*;
import shop.repository.*;
import shop.service.ConfigReader;
import shop.service.NotificationService;
import shop.service.OrderService;

public class Application {
    public static void main(String[] args) {
        // Читаем конфиг (значения подставлены Maven-профилем)
        ConfigReader config = new ConfigReader();

        System.out.println("=".repeat(50));
        System.out.println("  " + config.getShopName());
        System.out.println("  Окружение: " + config.getEnv());
        System.out.println("  Валюта:    " + config.get("shop.currency"));
        System.out.println("  Поддержка: " + config.get("shop.support.email"));
        System.out.println("=".repeat(50));

        // --- Обычная логика магазина ---
        ProductRepository productRepo = new InMemoryProductRepository();
        OrderRepository orderRepo = new InMemoryOrderRepository();
        NotificationService notificationService = new NotificationService();
        OrderService service = new OrderService(orderRepo, productRepo, notificationService);

        Product laptop = new Product(1, "Ноутбук", 1000, 5);
        Product phone = new Product(2, "Смартфон", 700, 3);
        productRepo.save(laptop);
        productRepo.save(phone);

        Customer customer = new Customer(1, "Иван Петров", "ivan@example.com");

        System.out.println("\nТовары:");
        productRepo.findAll().forEach(p -> System.out.println("   " + p));

        Order order = service.createOrder(customer, laptop, 1);
        service.processPayment(order, new Payment(order.getTotal(), "CARD"));

        System.out.println("\nИтог: " + order);
    }
}
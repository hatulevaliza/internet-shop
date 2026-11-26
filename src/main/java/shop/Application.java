package shop;

import shop.model.*;
import shop.repository.*;
import shop.service.ConfigReader;
import shop.service.NotificationService;
import shop.service.OrderService;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        MetricsServer.start();
        ConfigReader config = new ConfigReader();

        System.out.println("=".repeat(50));
        System.out.println("  " + config.getShopName());
        System.out.println("  Окружение: " + config.getEnv());
        System.out.println("=".repeat(50));

        ProductRepository productRepo = new InMemoryProductRepository();
        OrderRepository orderRepo = new InMemoryOrderRepository();
        NotificationService notificationService = new NotificationService();
        OrderService service = new OrderService(orderRepo, productRepo, notificationService);

        Product laptop = new Product(1, "Ноутбук", 1000, 5);
        Product phone = new Product(2, "Смартфон", 700, 3);
        productRepo.save(laptop);
        productRepo.save(phone);

        Customer customer = new Customer(1, "Иван Петров", "ivan@example.com");
        Customer customer2 = new Customer(2, "Иван Петров", "ivan@example.com");

        Order order = service.createOrder(customer, laptop, 1);
        Order order2 = service.createOrder(customer2, laptop, 3);
        Order order3 = service.createOrder(customer2, laptop, 1);

        try {
            service.createOrder(customer, laptop, 10);
        } catch (Exception e) {
            System.out.println("Поймана ошибка заказа: " + e.getMessage());
        }


        service.processPayment(order, new Payment(order.getTotal(), "CARD"));
        service.processPayment(order2, new Payment(order2.getTotal(), "CARD"));

        System.out.println("\nСервер работает! Проверяй http://localhost:8081/metrics");
        Thread.sleep(600000);
    }
}

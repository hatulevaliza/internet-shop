package shop.service;

import shop.model.Customer;
import shop.model.Order;
import shop.model.Payment;
import shop.model.Product;
import shop.repository.OrderRepository;
import shop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.Counter;
import shop.MetricsServer;

public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    private final Counter totalOrdersCounter;
    private final Counter successOrdersCounter;
    private final Counter errorOrdersCounter;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;

        this.totalOrdersCounter = MetricsServer.REGISTRY.counter("application_operations_total");
        this.successOrdersCounter = MetricsServer.REGISTRY.counter("application_operations_success_total");
        this.errorOrdersCounter = MetricsServer.REGISTRY.counter("application_operations_error_total");
    }

    public Order CreateOrder(Customer customer, Product product, int quantity) {
        totalOrdersCounter.increment();
        LOGGER.info("Получен запрос на оформление заказа от клиента: {}", customer.getName());

        if (!product.hasEnoughStock(quantity)) {
            errorOrdersCounter.increment();
            LOGGER.error("Ошибка обработки товара '{}': недостаточно на складе", product.getName());
            notificationService.sendNotification(customer.getEmail(),
                    "Ошибка: недостаточно товара '" + product.getName() + "'");
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }

        int orderId = orderRepository.findAll().size() + 1;
        Order order = new Order(orderId, customer);
        order.addItem(product, quantity);
        product.reduceStock(quantity);
        productRepository.update(product);
        orderRepository.save(order);
        customer.addOrder(order);
        notificationService.sendNotification(customer.getEmail(),
                "Заказ #" + order.getId() + " создан. Сумма: " + order.getTotal());
        successOrdersCounter.increment();
        LOGGER.info("Заказ #{} успешно оформлен. Сумма: {}", order.getId(), order.getTotal());
        return order;
    }

    public Order processPayment(Order order, Payment payment) {
        if (order.isPaid()) {
            throw new IllegalStateException("Заказ уже оплачен");
        }
        payment.process();
        if (payment.isSuccessful()) {
            order.setPayment(payment);
            order.setStatus(Order.OrderStatus.PAID);
            orderRepository.save(order);
            notificationService.sendNotification(order.getCustomer().getEmail(),
                    "Заказ #" + order.getId() + " оплачен. Спасибо за покупку!");
            LOGGER.info("Заказ #{} успешно оплачен.", order.getId());
        }
        return order;
    }

    public Order findOrder(int id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new IllegalArgumentException("Заказ не найден");
        }
        return order;
    }

    public void cancelOrder(int orderId) {
        Order order = findOrder(orderId);
        if (order.isPaid()) {
            throw new IllegalStateException("Нельзя отменить оплаченный заказ");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        LOGGER.info("Заказ #{} успешно отменен", orderId);
    }
}
package shop.service;

import shop.model.Customer;
import shop.model.Order;
import shop.model.Payment;
import shop.model.Product;
import shop.repository.OrderRepository;
import shop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    public Order createOrder(Customer customer, Product product, int quantity) {
        LOGGER.info("Получен запрос на оформление заказа от клиента: {}", customer.getName());

        if (!product.hasEnoughStock(quantity)) {
            LOGGER.error("Ошибка обработки товара '{}': недостаточно на складе (запрошено: {}, доступно: {})",
                    product.getName(), quantity, product.getStock());

            notificationService.sendNotification(customer.getEmail(),
                    "Ошибка: недостаточно товара '" + product.getName() +
                            "'. Доступно: " + product.getStock());
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }

        int orderId = orderRepository.findAll().size() + 1;
        Order order = new Order(orderId, customer);
        order.addItem(product, quantity);

        product.reduceStock(quantity);
        productRepository.update(product);

        orderRepository.save(order);
        customer.addOrder(order);

        // Эта строка была слишком длинной, теперь она разбита
        notificationService.sendNotification(customer.getEmail(),
                "Заказ #" + order.getId() + " создан. Сумма: " + order.getTotal());

        LOGGER.info("Заказ #{} успешно оформлен. Сумма: {}", order.getId(), order.getTotal());

        if (product.getStock() < 2) {
            LOGGER.warn("Внимание! Остаток товара '{}' подходит к концу. Осталось: {}",
                    product.getName(), product.getStock());
        }

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
            LOGGER.error("Заказ с ID {} не найден", id);
            throw new IllegalArgumentException("Заказ не найден");
        }
        return order;
    }

    public void cancelOrder(int orderId) {
        Order order = findOrder(orderId);
        if (order.isPaid()) {
            LOGGER.warn("Попытка отменить уже оплаченный заказ #{}", orderId);
            throw new IllegalStateException("Нельзя отменить оплаченный заказ");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        LOGGER.info("Заказ #{} успешно отменен", orderId);
    }
}
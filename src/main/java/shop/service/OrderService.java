package shop.service;

import shop.model.*;
import shop.model.Customer;
import shop.model.Order;
import shop.model.Payment;
import shop.model.Product;
import shop.repository.OrderRepository;
import shop.repository.ProductRepository;

public class OrderService {
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
        if (!product.hasEnoughStock(quantity)) {
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

        notificationService.sendNotification(customer.getEmail(),
                "Заказ #" + order.getId() + " создан. Сумма: " + order.getTotal());

        System.out.println("Заказ #" + order.getId() + " создан на " + order.getTotal());
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
    }
}
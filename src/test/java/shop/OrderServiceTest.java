package shop;

import shop.model.Customer;
import shop.model.Order;
import shop.model.Payment;
import shop.model.Product;
import shop.repository.InMemoryOrderRepository;
import shop.repository.InMemoryProductRepository;
import shop.service.NotificationService;
import shop.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderService — тесты")
class OrderServiceTest {

    private InMemoryOrderRepository orderRepo;
    private InMemoryProductRepository productRepo;
    private OrderService service;
    private Customer customer;

    @BeforeEach
    void setUp() {
        orderRepo = new InMemoryOrderRepository();
        productRepo = new InMemoryProductRepository();
        service = new OrderService(orderRepo, productRepo, new NotificationService());
        customer = new Customer(1, "Иван", "ivan@example.com");
    }

    // ============================================================
    // ТЕСТ 1 — успешный сценарий
    // ============================================================
    @Test
    @DisplayName("Тест 1: заказ успешно создан")
    void test1_successOrder() {
        Product laptop = new Product(1, "Ноутбук", 1000, 5);
        productRepo.save(laptop);

        Order order = service.createOrder(customer, laptop, 2);

        assertEquals(9998.0, order.getTotal(), 0.001,
                "Неверная сумма заказа");
        assertEquals(3, laptop.getStock(),
                "Неверный остаток товара");
        assertEquals(1, orderRepo.findAll().size(),
                "Заказ должен быть сохранён");
    }

    // ============================================================
    // ТЕСТ 2 — ошибочный сценарий (повторное действие запрещено)
    // ============================================================
    @Test
    @DisplayName("Тест 2: повторная оплата запрещена")
    void test2_doublePaymentForbidden() {
        Product laptop = new Product(1, "Ноутбук", 1000, 5);
        productRepo.save(laptop);

        Order order = service.createOrder(customer, laptop, 1);
        service.processPayment(order, new Payment(1000, "CARD"));

        assertThrows(
                IllegalStateException.class,
                () -> service.processPayment(order, new Payment(1000, "CARD")),
                "Повторная оплата должна быть запрещена"
        );
    }

    // ============================================================
    // ТЕСТ 3 — индивидуальное задание (нехватка товара)
    // ============================================================
    @Test
    @DisplayName("Тест 3: отказ при нехватке товара")
    void test3_notEnoughStock() {
        Product headphones = new Product(1, "Наушники", 150, 1);
        productRepo.save(headphones);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createOrder(customer, headphones, 10),
                "Должно быть исключение при нехватке товара"
        );

        assertEquals(1, headphones.getStock(),
                "Остаток не должен был измениться");
        assertTrue(orderRepo.findAll().isEmpty(),
                "Заказ не должен был создаться");
    }
}
package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class Bootstrap {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private static final BigDecimal DB_PRODUCT1_GROSS_PRICE = new BigDecimal("3.1");
    public static final String DB_PRODUCT1_NAME = "dbProduct1";
    public static final String DB_PRODUCT2_NAME = "dbProduct2";
    public static final String DB_PRODUCT3_NAME = "dbProduct3";
    public static final String DB_BASKET1_ID = "BASKET1";
    private static final String DB_NOT_EMPTY_BASKET_ID = "notEmptyBasket";
    public static final BigDecimal DB_BASKET1_TOTAL_PRICE = new BigDecimal("120");
    public static final LocalDateTime DATE_TIME = LocalDateTime.of(2024,1,1,0,0);
    public static final String DB_ORDER_ID1 = "ORDER1";
    public static final String DB_ORDER_ID2 = "ORDER2";
    public static final String DB_ORDER_ID3 = "ORDER3";
    public static final String DB_ORDER_ID4 = "ORDER4";
    public static final String DB_ORDER_ID5 = "ORDER5";
    public static final BigDecimal ORDER1_TOTAL_PRICE = new BigDecimal("100");
    public static final String DB_USER_LOGIN = "user1";
    private final List<Basket> baskets = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<Order> user1_orders = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public Bootstrap(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, BasketRepository basketRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
    }

    public void loadData() {
        log.debug("Loading products...");
        Product product1 = new Product();
        product1.setName(DB_PRODUCT1_NAME);
        product1.setAmount(5);
        product1.setNetPrice(new BigDecimal("1.5"));
        product1.setGrossPrice(DB_PRODUCT1_GROSS_PRICE);
        product1.setDescription("testDescription1");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName(DB_PRODUCT2_NAME);
        product2.setAmount(7);
        product2.setNetPrice(new BigDecimal("2.3"));
        product2.setGrossPrice(new BigDecimal("5.1"));
        product2.setDescription("testDescription2");
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName(DB_PRODUCT3_NAME);
        product3.setAmount(12);
        product3.setNetPrice(new BigDecimal("5.7"));
        product3.setGrossPrice(new BigDecimal("2.2"));
        product3.setDescription("testDescription3");
        productRepository.save(product3);

        products.addAll(List.of(product1,product2,product3));

        log.debug("loading Baskets...");

        Basket basket1 = new Basket();
        basket1.setBasketId(DB_BASKET1_ID);
        basket1.setTotalPrice(DB_BASKET1_TOTAL_PRICE);
        basketRepository.save(basket1);

        Basket basket2 = new Basket();
        Basket.createBasketId(basket2);
        basket2.setTotalPrice(new BigDecimal("210"));
        basketRepository.save(basket2);

        Basket notEmptyBasket = new Basket();
        notEmptyBasket.setBasketId(DB_NOT_EMPTY_BASKET_ID);
        notEmptyBasket.setProducts(Map.of(product1,1));
        notEmptyBasket.setTotalPrice(new BigDecimal("320"));
        basketRepository.save(notEmptyBasket);

        baskets.addAll(List.of(basket1,basket2,notEmptyBasket));

        log.debug("Loading Orders...");

        Order order1 = new Order();
        order1.setOrderId(DB_ORDER_ID1);
        order1.setOrderDate(DATE_TIME);
        order1.setAdmissionDate(DATE_TIME);
        order1.setRealizationDate(DATE_TIME);
        order1.setPaymentDate(DATE_TIME);
        order1.setTotalPrice(ORDER1_TOTAL_PRICE);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setOrderId(DB_ORDER_ID2);
        order2.setOrderDate(DATE_TIME);
        order2.setTotalPrice(new BigDecimal("120"));
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setOrderId(DB_ORDER_ID3);
        order3.setOrderDate(DATE_TIME);
        order3.setTotalPrice(new BigDecimal("75"));
        orderRepository.save(order3);

        Order order4 = new Order();
        order4.setOrderId(DB_ORDER_ID4);
        order4.setOrderDate(DATE_TIME);
        order4.setTotalPrice(new BigDecimal("99"));
        orderRepository.save(order4);

        Order order5 = new Order();
        order5.setOrderId(DB_ORDER_ID5);
        order5.setOrderDate(DATE_TIME);
        order5.setTotalPrice(new BigDecimal("3"));
        orderRepository.save(order5);

        orders.addAll(List.of(order5, order1, order2, order3, order4));

        log.debug("Loading Users...");

        User user1 = new User();
        user1.setBasket(notEmptyBasket);
        user1.setLogin(DB_USER_LOGIN);
        user1.setPassword("password1");
        user1.addOrder(order1);
        user1.addOrder(order3);
        user1.addOrder(order5);
        userRepository.save(user1);

        user1_orders.addAll(user1.getOrders());

        User user2 = new User();
//        user2.setBasket(new Basket());
        user2.setLogin("user2");
        user2.setPassword("password2");
        user2.addOrder(order2);
        user2.addOrder(order4);
        userRepository.save(user2);

        users.addAll(List.of(user1, user2));
    }
    public List<Product> getProducts() {
        return products;
    }
    public List<Basket> getBaskets() {
        return baskets;
    }
    public List<Order> getOrders() {
        return orders;
    }

    public List<Order> getUser1_orders() {
        return user1_orders;
    }

    public List<User> getUsers() {
        return users;
    }
}

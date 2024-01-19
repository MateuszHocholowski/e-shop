package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BootstrapUsersAndOrders {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public BootstrapUsersAndOrders(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024,1,1,0,0);
    public static final String DB_ORDER_ID1 = "ORDER1";
    public static final String DB_ORDER_ID2 = "ORDER2";
    public static final String DB_ORDER_ID3 = "ORDER3";
    public static final String DB_ORDER_ID4 = "ORDER4";
    public static final String DB_ORDER_ID5 = "ORDER5";
    public static final String DB_USER_LOGIN = "user1";
    private List<Order> orders;
    private List<Order> user1_orders;
    private List<User> users = new ArrayList<>();

    public void loadData() {

        log.debug("Loading Orders...");

        Order order1 = new Order();
        order1.setOrderId(DB_ORDER_ID1);
        order1.setOrderDate(DATE_TIME);
        order1.setAdmissionDate(DATE_TIME);
        order1.setRealizationDate(DATE_TIME);
        order1.setPaymentDate(DATE_TIME);
        order1.setTotalPrice(new BigDecimal("100"));
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

        orders = List.of(order5, order1, order2, order3, order4);

        log.debug("Loading Users...");

        User user1 = new User();
        user1.setLogin(DB_USER_LOGIN);
        user1.setPassword("password1");
        user1.addOrder(order1);
        user1.addOrder(order3);
        user1.addOrder(order5);
        userRepository.save(user1);

        user1_orders = user1.getOrders();

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password2");
        user2.addOrder(order2);
        user2.addOrder(order4);
        userRepository.save(user2);

        users = List.of(user1, user2);

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
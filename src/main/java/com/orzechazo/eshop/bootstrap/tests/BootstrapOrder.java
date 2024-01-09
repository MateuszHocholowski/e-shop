package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
public class BootstrapOrder {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public BootstrapOrder(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024,1,1,0,0);

    public void loadData() {
        setData();
    }

    private void setData() {
        log.debug("Loading User Data");

        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("password1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password2");
        userRepository.save(user2);

        log.debug("Users loaded: " + userRepository.count());

        log.debug("Loading Orders Data");

        Order order1 = new Order();
        Order.createOrderId(order1);
        order1.setOrderDate(DATE_TIME);
        order1.setAdmissionDate(DATE_TIME);
        order1.setRealizationDate(DATE_TIME);
        order1.setPaymentDate(DATE_TIME);
        order1.setTotalPrice(new BigDecimal("100"));
        orderRepository.save(order1);

        Order order2 = new Order();
        Order.createOrderId(order2);
        order2.setOrderDate(DATE_TIME);
        order2.setTotalPrice(new BigDecimal("120"));
        orderRepository.save(order2);

        Order order3 = new Order();
        Order.createOrderId(order3);
        order3.setOrderDate(DATE_TIME);
        order3.setTotalPrice(new BigDecimal("75"));
        orderRepository.save(order3);

        Order order4 = new Order();
        Order.createOrderId(order4);
        order4.setOrderDate(DATE_TIME);
        order4.setTotalPrice(new BigDecimal("99"));
        orderRepository.save(order4);

        Order order5 = new Order();
        Order.createOrderId(order5);
        order5.setOrderDate(DATE_TIME);
        order5.setTotalPrice(new BigDecimal("3"));
        orderRepository.save(order5);

        log.debug("Orders loaded: " + orderRepository.count());

        order1.setUser(user1);
        order2.setUser(user2);
        order3.setUser(user1);
        order4.setUser(user2);
        order5.setUser(user1);
    }
}

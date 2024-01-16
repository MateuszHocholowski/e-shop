package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BootstrapUser {

    private final UserRepository userRepository;
    private List<User> users = new ArrayList<>();
    public static final String DB_USER_NAME = "user1";

    public BootstrapUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loadData() {
        log.debug("Loading User Data");
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        Order.createOrderId(order);

        User user1 = new User();
        user1.setLogin(DB_USER_NAME);
        user1.setPassword("password1");
        user1.addOrder(order);
        userRepository.save(user1);

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password2");
        userRepository.save(user2);

        users = List.of(user1,user2);
    }

    public List<User> getUsers() {
        return users;
    }
}



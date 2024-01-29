package com.orzechazo.eshop.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void addOrder() {
        User user = new User();
        user.setLogin("login");
        Order order = new Order();
        order.setOrderId("1");
        //when
        user.addOrder(order);
        //then
        assertEquals(1, user.getOrders().size());
        assertEquals("1",user.getOrders().get(0).getOrderId());
        assertEquals("login",order.getUser().getLogin());
    }
}
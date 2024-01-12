package com.orzechazo.eshop.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    @Test
    void setUser() {
        //given
        Order order = new Order();
        order.setOrderId("1");
        User user = new User();
        //when
        order.setUser(user);
        //then
        assertEquals(1,user.getOrders().size());
        assertEquals("1",user.getOrders().get(0).getOrderId());
    }
    @Test
    void setUserTwice() {
        //given
        Order order = new Order();
        order.setOrderId("1");
        User user = new User();
        //when
        order.setUser(user);
        order.setUser(user);
        //then
        assertEquals(1,user.getOrders().size());
        assertEquals("1",user.getOrders().get(0).getOrderId());
    }
}
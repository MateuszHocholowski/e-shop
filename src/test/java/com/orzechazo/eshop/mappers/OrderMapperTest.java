package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static final LocalDateTime DATE = LocalDateTime.now();
    OrderMapper mapper = OrderMapper.INSTANCE;

    @Test
    void orderDtoToOrder() {
        //given
        Order expectedOrder = new Order();
        expectedOrder.setOrderDate(DATE);
        expectedOrder.setOrderId(1L);
        expectedOrder.setTotalPrice(new BigDecimal("12"));
        expectedOrder.setPaymentDate(DATE);
        expectedOrder.setAdmissionDate(DATE);
        expectedOrder.setRealizationDate(DATE);
        expectedOrder.setUser(new User());
        expectedOrder.setProducts(new ArrayList<>());

        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("12"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .user(UserDto.builder().build())
                .products(new ArrayList<>())
                .build();
        //when
        Order mappedOrder = mapper.orderDtoToOrder(orderDto);
        //then
        assertEquals(expectedOrder,mappedOrder);
    }

    @Test
    void orderDtoToOrderNotEquals() {
        //given
        Order expectedOrder = new Order();
        expectedOrder.setOrderDate(DATE);
        expectedOrder.setOrderId(1L);
        expectedOrder.setTotalPrice(new BigDecimal("12"));
        expectedOrder.setPaymentDate(DATE);
        expectedOrder.setAdmissionDate(DATE);
        expectedOrder.setRealizationDate(DATE);
        expectedOrder.setUser(new User());
        expectedOrder.setProducts(new ArrayList<>());

        OrderDto orderDto = OrderDto.builder()
                .orderId(1L)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("13"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .user(UserDto.builder().build())
                .products(new ArrayList<>())
                .build();
        //when
        Order mappedOrder = mapper.orderDtoToOrder(orderDto);
        //then
        assertNotEquals(expectedOrder,mappedOrder);
    }
    @Test
    void orderToOrderDto() {
        //given
        Order order = new Order();
        order.setOrderDate(DATE);
        order.setOrderId(1L);
        order.setTotalPrice(new BigDecimal("12"));
        order.setPaymentDate(DATE);
        order.setAdmissionDate(DATE);
        order.setRealizationDate(DATE);
        order.setUser(new User());
        order.setProducts(new ArrayList<>());

        OrderDto expectedDto = OrderDto.builder()
                .orderId(1L)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("12"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .user(UserDto.builder().build())
                .products(new ArrayList<>())
                .build();
        //when
        OrderDto mappedDto = mapper.orderToOrderDto(order);
        //then
        assertEquals(expectedDto,mappedDto);
    }

    @Test
    void orderToOrderDtoNotEquals() {
        //given
        Order order = new Order();
        order.setOrderDate(DATE);
        order.setOrderId(1L);
        order.setTotalPrice(new BigDecimal("12"));
        order.setPaymentDate(DATE);
        order.setAdmissionDate(DATE);
        order.setRealizationDate(DATE);
        order.setUser(new User());
        order.setProducts(new ArrayList<>());

        OrderDto expectedDto = OrderDto.builder()
                .orderId(1L)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("13"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .user(UserDto.builder().build())
                .products(new ArrayList<>())
                .build();
        //when
        OrderDto mappedDto = mapper.orderToOrderDto(order);
        //then
        assertNotEquals(expectedDto,mappedDto);
    }
    @Test
    void orderDtoToOrderNull() {
        //when
        Order mappedOrder = mapper.orderDtoToOrder(null);
        //then
        assertNull(mappedOrder);
    }

    @Test
    void orderToOrderDtoNull() {
        //when
        OrderDto mappedDto = mapper.orderToOrderDto(null);
        //then
        assertNull(mappedDto);
    }
}
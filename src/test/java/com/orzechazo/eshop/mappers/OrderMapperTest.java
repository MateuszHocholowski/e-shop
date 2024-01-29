package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static final LocalDateTime DATE = LocalDateTime.now();
    public static final String ORDER_ID = "1";
    public static final String USER_LOGIN = "userLogin";
    OrderMapper mapper = OrderMapper.INSTANCE;

    @Test
    void orderDtoToOrder() {
        User user = new User();
        user.setLogin(USER_LOGIN);
        //given
        Order expectedOrder = new Order();
        expectedOrder.setOrderDate(DATE);
        expectedOrder.setOrderId(ORDER_ID);
        expectedOrder.setTotalPrice(new BigDecimal("12"));
        expectedOrder.setPaymentDate(DATE);
        expectedOrder.setAdmissionDate(DATE);
        expectedOrder.setRealizationDate(DATE);
        expectedOrder.setOrderStatus(OrderStatus.PENDING);
        expectedOrder.setUser(user);
        expectedOrder.setProducts(new ArrayList<>());

        OrderDto orderDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("12"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .userLogin(USER_LOGIN)
                .orderStatus(OrderStatus.PENDING)
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
        expectedOrder.setOrderId(ORDER_ID);
        expectedOrder.setTotalPrice(new BigDecimal("12"));
        expectedOrder.setPaymentDate(DATE);
        expectedOrder.setAdmissionDate(DATE);
        expectedOrder.setRealizationDate(DATE);
        expectedOrder.setOrderStatus(OrderStatus.PENDING);
        expectedOrder.setUser(new User());
        expectedOrder.setProducts(new ArrayList<>());

        OrderDto orderDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("13"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .orderStatus(OrderStatus.PENDING)
                .userLogin(USER_LOGIN)
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
        User user = new User();
        user.setLogin(USER_LOGIN);

        Order order = new Order();
        order.setOrderDate(DATE);
        order.setOrderId(ORDER_ID);
        order.setTotalPrice(new BigDecimal("12"));
        order.setPaymentDate(DATE);
        order.setAdmissionDate(DATE);
        order.setRealizationDate(DATE);
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setUser(user);
        order.setProducts(new ArrayList<>());

        OrderDto expectedDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("12"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .userLogin(USER_LOGIN)
                .orderStatus(OrderStatus.PROCESSING)
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
        order.setOrderId(ORDER_ID);
        order.setTotalPrice(new BigDecimal("12"));
        order.setPaymentDate(DATE);
        order.setAdmissionDate(DATE);
        order.setRealizationDate(DATE);
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUser(new User());
        order.setProducts(new ArrayList<>());

        OrderDto expectedDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("13"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .orderStatus(OrderStatus.CANCELLED)
                .userLogin(USER_LOGIN)
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
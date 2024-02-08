package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private static final LocalDateTime DATE = LocalDateTime.now();
    public static final String ORDER_ID = "1";
    public static final String USER_LOGIN = "userLogin";
    private static final String NAME_1 = "product1";
    private static final String NAME_2 = "product2";
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

        OrderDto orderDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("12"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .userLogin(USER_LOGIN)
                .orderStatus(OrderStatus.PENDING)
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

        OrderDto orderDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("13"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .orderStatus(OrderStatus.PENDING)
                .userLogin(USER_LOGIN)
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
        order.setProducts(new HashMap<>());

        OrderDto expectedDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("12"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .userLogin(USER_LOGIN)
                .orderStatus(OrderStatus.PROCESSING)
                .productNamesMap(new HashMap<>())
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
        order.setProducts(new HashMap<>());

        OrderDto expectedDto = OrderDto.builder()
                .orderId(ORDER_ID)
                .orderDate(DATE)
                .totalPrice(new BigDecimal("13"))
                .paymentDate(DATE)
                .realizationDate(DATE)
                .admissionDate(DATE)
                .orderStatus(OrderStatus.CANCELLED)
                .userLogin(USER_LOGIN)
                .productNamesMap(new HashMap<>())
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

    @Test
    void testMappingOrderProductsToOrderProductNamesMap() {
        //given
        Product product1 = new Product();
        product1.setName(NAME_1);
        Product product2 = new Product();
        product2.setName(NAME_2);

        Map<Product, Integer> orderProducts = Map.of(product1,1,product2,2);

        Order order = new Order();
        order.setOrderId(ORDER_ID);
        order.setProducts(orderProducts);
        //when
        OrderDto mappedDto = mapper.orderToOrderDto(order);
        //then
        assertEquals(1,mappedDto.getProductNamesMap().get(NAME_1));
        assertEquals(2,mappedDto.getProductNamesMap().get(NAME_2));
    }
}
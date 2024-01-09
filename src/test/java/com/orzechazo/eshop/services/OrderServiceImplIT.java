package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapOrder;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceImplIT {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    private OrderServiceImpl orderService;
    private UserServiceImpl userService;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024,1,1,0,0);
    private String ORDER_ID;
    @BeforeEach
    void setUp() {
        BootstrapOrder bootstrapOrder = new BootstrapOrder(orderRepository,userRepository);
        bootstrapOrder.loadData();

        orderService = new OrderServiceImpl(orderRepository);
        userService = new UserServiceImpl(userRepository);
        ORDER_ID = orderService.getAllOrders().get(0).getOrderId();
    }

    @Test
    void getAllOrders() {
        //given
        long count = orderRepository.count();
        //when
        List<OrderDto> orderDtos = orderService.getAllOrders();
        //then
        assertEquals(count, orderDtos.size());
        assertEquals(new BigDecimal("100"),orderDtos.get(0).getTotalPrice());
        assertEquals(DATE_TIME,orderDtos.get(1).getOrderDate());
    }

    @Test
    void getOrderByOrderId() {
        //when
        OrderDto returnedDto = orderService.getOrderDtoByOrderId(ORDER_ID);
        //then
        assertEquals(DATE_TIME,returnedDto.getOrderDate());
        assertEquals(DATE_TIME,returnedDto.getPaymentDate());
        assertEquals(DATE_TIME,returnedDto.getAdmissionDate());
        assertEquals(DATE_TIME,returnedDto.getRealizationDate());
        assertEquals(new BigDecimal("100"),returnedDto.getTotalPrice());
        assertEquals("user1",returnedDto.getUser().getLogin());
    }

    @Test
    void getOrderByOrderIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                ()-> orderService.getOrderDtoByOrderId("1"));
        assertEquals("Order with id: 1 doesn't exist in database.",exception.getMessage());
    }

    @Test
    void getOrdersByUser() {
        UserDto userDto = userService.getUserByLogin("user1");

        List<OrderDto> returnedDtos = orderService.getOrdersByUser(userDto);
        //then
        assertEquals(3, returnedDtos.size());
    }

    @Test
    void getOrdersByUserNotFound() {
        List<OrderDto> returnedDtos = orderService.getOrdersByUser(
                UserDto.builder().login("test").build());
        assertEquals(0,returnedDtos.size()); //todo change getOrdersByUser method
    }

    @Test
    void createOrder() {
        //given
        long count = orderRepository.count();
        OrderDto orderDto = OrderDto.builder().orderDate(DATE_TIME)
                .totalPrice(new BigDecimal("2")).build();
        //when
        OrderDto createdDto = orderService.createOrder(orderDto);
        //then
        assertEquals(DATE_TIME,createdDto.getOrderDate());
        assertEquals(new BigDecimal("2"),createdDto.getTotalPrice());
        assertEquals(count+1,orderRepository.count());
        assertNotNull(createdDto.getOrderId());
    }

    @Test
    void createOrderExistingId() {
        OrderDto orderDto = OrderDto.builder().orderId(ORDER_ID).build();
        Exception exception = assertThrows(BadRequestException.class,
                () -> orderService.createOrder(orderDto));
        assertEquals("Order already has an id: " + ORDER_ID,exception.getMessage());
    }

    @Test
    void updateOrder() {
        //given
                OrderDto orderToUpdate = OrderDto.builder().orderId(ORDER_ID).orderDate(DATE_TIME.plusDays(1))
                .totalPrice(new BigDecimal("111")).build();
        //when
        OrderDto updatedDto = orderService.updateOrder(ORDER_ID, orderToUpdate);
        OrderDto expectedDto = orderService.getOrderDtoByOrderId(ORDER_ID);
        //then
        assertEquals(expectedDto,updatedDto);
        assertEquals(new BigDecimal("111"),updatedDto.getTotalPrice());
        assertEquals(DATE_TIME.plusDays(1),updatedDto.getOrderDate());
        assertNull(updatedDto.getRealizationDate());
    }

    @Test
    void updateOrderNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                ()-> orderService.updateOrder("1", OrderDto.builder().orderDate(DATE_TIME).build()));
        assertEquals("Order with id: 1 doesn't exist in database.",exception.getMessage());
    }

    @Test
    void deleteOrderByOrderId() {
        long count = orderRepository.count();
        //when
        orderService.deleteOrderByOrderId(ORDER_ID);
        //then
        assertEquals(count-1, orderRepository.count());
    }
}
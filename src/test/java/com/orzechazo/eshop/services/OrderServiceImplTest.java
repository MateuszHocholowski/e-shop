package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private final static BigDecimal PRICE = new BigDecimal("13");
    public static final String ORDER_ID = "1";
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private OrderRepository orderRepository;

    @Test
    void getAllOrders() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        Order order2 = new Order();
        order2.setTotalPrice(PRICE);
        List<Order> orders = List.of(order1,order2);
        when(orderRepository.findAll()).thenReturn(orders);
        //when
        List<OrderDto> returnedDtos = orderService.getAllOrders();
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals(PRICE,returnedDtos.get(0).getTotalPrice());
        assertEquals(PRICE,returnedDtos.get(1).getTotalPrice());
    }

    @Test
    void getOrderByOrderId() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        order1.setOrderId(ORDER_ID);
        when(orderRepository.findByOrderId(any())).thenReturn(Optional.of(order1));
        //when
        OrderDto returnedDto = orderService.getOrderByOrderId(ORDER_ID);
        //then
        assertEquals(PRICE,returnedDto.getTotalPrice());
        assertEquals(ORDER_ID,returnedDto.getOrderId());
    }
    @Test
    void getOrderByOrderIdBadRequest() {
        Exception exception = assertThrows(ResourceNotFoundException.class,()-> orderService.getOrderByOrderId(ORDER_ID));
        assertEquals("Order with id: 1 doesn't exist in database.", exception.getMessage());
    }
    @Test
    void getOrdersByUser() {
        //given
        OrderDto orderDto1 = OrderDto.builder().totalPrice(PRICE).build();
        OrderDto orderDto2 = OrderDto.builder().totalPrice(PRICE).build();
        UserDto userDto = UserDto.builder().login("login1").orders(List.of(orderDto1,orderDto2)).build();
        when(userService.getUserByLogin(anyString())).thenReturn(userDto);
        //when
        List<OrderDto> returnedDtos = orderService.getOrdersByUser("login1");
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals(PRICE,returnedDtos.get(0).getTotalPrice());
    }

    @Test
    void createOrder() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        when(orderRepository.save(any())).thenReturn(order1);
        //when
        OrderDto createdDto = orderService.createOrder(OrderDto.builder().build());
        //then
        assertEquals(PRICE,createdDto.getTotalPrice());
        verify(orderRepository,times(1)).save(any());
    }

    @Test
    void createOrderExistingId() {
        Exception exception = assertThrows(BadRequestException.class,
                () -> orderService.createOrder(OrderDto.builder().orderId(ORDER_ID).build()));
        assertEquals("Order already has an id: 1",exception.getMessage());
    }

    @Test
    void updateOrder() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        when(orderRepository.save(any())).thenReturn(order1);
        //when
        OrderDto updatedDto = orderService.updateOrder(ORDER_ID, OrderDto.builder().build());
        //then
        assertEquals(PRICE,updatedDto.getTotalPrice());
        verify(orderRepository,times(1)).save(any());
    }

    @Test
    void deleteOrderByOrderId() {
        //when
        orderService.deleteOrderByOrderId(ORDER_ID);
        //then
        verify(orderRepository,times(1)).deleteByOrderId(any());
    }
}
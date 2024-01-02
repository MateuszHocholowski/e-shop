package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private final static BigDecimal PRICE = new BigDecimal("13");
    @InjectMocks
    private OrderServiceImpl orderService;
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
    void getOrderById() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order1));
        //when
        OrderDto returnedDto = orderService.getOrderById(1L);
        //then
        assertEquals(PRICE,returnedDto.getTotalPrice());
    }

    @Test
    void getOrdersByUser() {
        //given
        User user = new User();
        user.setLogin("login1");
        UserDto userDto = UserDto.builder().login("login1").build();
        Order order1 = new Order();
        order1.setUser(user);
        order1.setTotalPrice(PRICE);
        Order order2 = new Order();
        order2.setUser(user);
        order2.setTotalPrice(PRICE);
        List<Order> orders = List.of(order1,order2);
        when(orderRepository.findAll()).thenReturn(orders);
        //when
        List<OrderDto> returnedDtos = orderService.getOrdersByUser(userDto);
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals(PRICE,returnedDtos.get(0).getTotalPrice());
        assertEquals("login1",returnedDtos.get(1).getUser().getLogin());
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
    void createOrderExistingOrder() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        OrderDto orderDto = OrderDto.builder().totalPrice(PRICE).build();
        when(orderRepository.findAll()).thenReturn(List.of(order1));
        //when
        OrderDto createdDto = orderService.createOrder(orderDto);
        //then
        assertNull(createdDto);
        verify(orderRepository,times(0)).save(any());
    }

    @Test
    void updateOrder() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        when(orderRepository.save(any())).thenReturn(order1);
        //when
        OrderDto updatedDto = orderService.updateOrder(1L, OrderDto.builder().build());
        //then
        assertEquals(PRICE,updatedDto.getTotalPrice());
        verify(orderRepository,times(1)).save(any());
    }

    @Test
    void deleteOrderById() {
        //when
        orderService.deleteOrderById(1L);
        //then
        verify(orderRepository,times(1)).deleteById(anyLong());
    }
}
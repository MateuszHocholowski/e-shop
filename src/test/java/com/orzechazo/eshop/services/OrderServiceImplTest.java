package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
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
    void getOrderByOrderId() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        order1.setOrderId(1L);
        when(orderRepository.findByOrderId(anyLong())).thenReturn(Optional.of(order1));
        //when
        OrderDto returnedDto = orderService.getOrderByOrderId(1L);
        //then
        assertEquals(PRICE,returnedDto.getTotalPrice());
        assertEquals(1L,returnedDto.getOrderId());
    }
    @Test
    void getOrderByOrderIdBadRequest() {
        Exception exception = assertThrows(ResourceNotFoundException.class,()-> orderService.getOrderByOrderId(6L));
        assertEquals("Order with id: 6 doesn't exist in database.", exception.getMessage());
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
        order1.setOrderId(5L);
        OrderDto orderDto = OrderDto.builder().orderId(5L).build();
        when(orderRepository.findByOrderId(anyLong())).thenReturn(Optional.of(order1));
        //when
        Exception exception = assertThrows(BadRequestException.class,() -> orderService.createOrder(orderDto));
        //then
        assertEquals("Order: 5 is already in database.",exception.getMessage());
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
        orderService.deleteOrderByOrderId(1L);
        //then
        verify(orderRepository,times(1)).deleteById(anyLong());
    }
}
package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Long id);
    List<OrderDto> getOrdersByUser(UserDto userDto);
    OrderDto createOrder(OrderDto orderDto);
    OrderDto updateOrder(Long id,OrderDto orderDto);
    OrderDto saveOrderAndReturnDto(Order order);
    void deleteOrderById(Long id);

}

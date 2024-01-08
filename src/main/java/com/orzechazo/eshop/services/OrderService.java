package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAllOrders();
    OrderDto getOrderByOrderId(Long id);
    List<OrderDto> getOrdersByUser(UserDto userDto);
    OrderDto createOrder(OrderDto orderDto);
    OrderDto updateOrder(Long id,OrderDto orderDto);
    void deleteOrderByOrderId(Long id);

}

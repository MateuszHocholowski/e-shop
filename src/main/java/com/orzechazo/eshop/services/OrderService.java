package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAllOrders();
    OrderDto getOrderByOrderId(String orderId);
    List<OrderDto> getOrdersByUser(String userLogin);
    OrderDto createOrder(OrderDto orderDto);
    OrderDto updateOrder(String id,OrderDto orderDto);
    void deleteOrderByOrderId(String orderId);

}

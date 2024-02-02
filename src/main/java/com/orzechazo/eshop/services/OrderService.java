package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<OrderDto> getAllOrders();
    OrderDto getOrderDtoByOrderId(String orderId);
    OrderDto createOrder(String userLogin);
    void deleteOrderByOrderId(String orderId);

}

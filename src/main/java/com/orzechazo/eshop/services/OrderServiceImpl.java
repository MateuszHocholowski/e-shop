package com.orzechazo.eshop.services;

import com.orzechazo.eshop.mappers.OrderMapper;
import com.orzechazo.eshop.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
}

package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.OrderMapper;
import com.orzechazo.eshop.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderServiceImpl(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::orderToOrderDto)
                .toList();
    }

    @Override
    public OrderDto getOrderDtoByOrderId(String orderId) {
        return orderMapper.orderToOrderDto(getOrderByOrderId(orderId));
    }

    @Transactional
    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order newOrder = orderMapper.orderDtoToOrder(orderDto);
        Order.createOrderId(newOrder);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder = userService.addOrder(orderDto.getUserLogin(), newOrder);
        return saveOrderAndReturnDto(newOrder);
    }

    private OrderDto saveOrderAndReturnDto(Order order) {
        return orderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrderByOrderId(String orderId) {
        Order orderToDelete = getOrderByOrderId(orderId);
        userService.deleteOrder(orderToDelete);
        orderRepository.delete(orderToDelete);
    }
    private Order getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId).orElseThrow(
                ()-> new ResourceNotFoundException("Order with id: " + orderId + " doesn't exist in database."));
    }
}

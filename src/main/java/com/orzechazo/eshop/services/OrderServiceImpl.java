package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.OrderMapper;
import com.orzechazo.eshop.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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

    @Override
    public List<OrderDto> getOrdersByUser(UserDto userDto) {
        return orderRepository.findAll().stream()
                .map(orderMapper::orderToOrderDto)
                .filter(orderDto -> orderDto.getUser().equals(userDto))
                .toList(); //todo change the method to improve performance
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order newOrder = orderMapper.orderDtoToOrder(orderDto);
        Order.createOrderId(newOrder);
        return saveOrderAndReturnDto(newOrder);
    }

    @Override
    public OrderDto updateOrder(String orderId, OrderDto orderDto) {
        Order currentOrder = getOrderByOrderId(orderId);
        Order updateOrder = orderMapper.orderDtoToOrder(orderDto);
        updateOrder.setId(currentOrder.getId());
        return saveOrderAndReturnDto(updateOrder);
    }

    private OrderDto saveOrderAndReturnDto(Order order) {
        return orderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrderByOrderId(String orderId) {
        orderRepository.deleteByOrderId(orderId);
    }
    private Order getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId).orElseThrow(
                ()-> new ResourceNotFoundException("Order with id: " + orderId + " doesn't exist in database."));
    }
}

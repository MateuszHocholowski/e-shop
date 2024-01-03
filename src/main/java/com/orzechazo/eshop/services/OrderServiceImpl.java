package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
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
    public OrderDto getOrderByOrderId(Long orderId) {
        return orderMapper.orderToOrderDto(orderRepository.findByOrderId(orderId)
                .orElseThrow(()-> new ResourceNotFoundException("Order with id: " + orderId
                        + " doesn't exist in database.")));
    }

    @Override
    public List<OrderDto> getOrdersByUser(UserDto userDto) {
        return orderRepository.findAll().stream()
                .map(orderMapper::orderToOrderDto)
                .filter(productDto -> productDto.getUser().equals(userDto))
                .toList();
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order newOrder = orderMapper.orderDtoToOrder(orderDto);
        if(orderRepository.findByOrderId(orderDto.getOrderId()).isEmpty()) {
            return saveOrderAndReturnDto(newOrder);
        } else {
            throw new BadRequestException("Order: " + orderDto.getOrderId() + " is already in database.");
        }
    }

    @Override
    public OrderDto updateOrder(Long orderId, OrderDto orderDto) {
        Order updateOrder = orderMapper.orderDtoToOrder(orderDto);
        updateOrder.setOrderId(orderId);
        return saveOrderAndReturnDto(updateOrder);
    }

    private OrderDto saveOrderAndReturnDto(Order order) {
        return orderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrderByOrderId(Long id) {
        orderRepository.deleteById(id);
    }
}

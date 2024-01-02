package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
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
    public OrderDto getOrderById(Long id) {
        return orderMapper.orderToOrderDto(orderRepository.findById(id).orElseThrow(RuntimeException::new));
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
        if(!orderRepository.findAll().contains(newOrder)) {
            return saveOrderAndReturnDto(newOrder);
        } else {
            System.out.println("That order is already in database");
        }
        return null;
    }

    @Override
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order updateOrder = orderMapper.orderDtoToOrder(orderDto);
        updateOrder.setId(id);
        return saveOrderAndReturnDto(updateOrder);
    }

    @Override
    public OrderDto saveOrderAndReturnDto(Order order) {
        return orderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}

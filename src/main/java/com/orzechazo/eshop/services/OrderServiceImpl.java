package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.OrderMapper;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BasketService basketService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, BasketService basketService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.basketService = basketService;
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
    public OrderDto createOrder(String userLogin) {
        Order newOrder = new Order();
        Order.createOrderId(newOrder);
        newOrder.setOrderDate(LocalDateTime.now());

        User currentUser = getUserByLogin(userLogin);
        Basket usersBasket = currentUser.getBasket();

        turnBasketIntoOrder(usersBasket, newOrder);

        return addOrderToUser(currentUser, newOrder);
    }

    private OrderDto saveOrderAndReturnDto(Order order) {
        return orderMapper.orderToOrderDto(orderRepository.save(order));
    }

    @Override
    public void deleteOrderByOrderId(String orderId) {
        Order orderToDelete = getOrderByOrderId(orderId);
        User currentUser = orderToDelete.getUser();
        currentUser.getOrders().remove(orderToDelete);
        orderRepository.delete(orderToDelete);
    }
    private OrderDto addOrderToUser(User currentUser, Order orderToAdd) {
        currentUser.addOrder(orderToAdd);

        Basket newUserBasket = new Basket();
        Basket.createBasketId(newUserBasket);
        currentUser.setBasket(newUserBasket);

        return saveOrderAndReturnDto(orderToAdd);
    }
    private void turnBasketIntoOrder(Basket usersBasket, Order newOrder) {
        if (usersBasket.getProducts().isEmpty()) {
            throw new BadRequestException("Cannot create order: basket is empty");
        }
        newOrder.setProducts(Map.copyOf(usersBasket.getProducts()));
        newOrder.setTotalPrice(usersBasket.getTotalPrice());
        basketService.deleteBasket(usersBasket.getBasketId());
    }
    private Order getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId).orElseThrow(
                ()-> new ResourceNotFoundException("Order with id: " + orderId + " doesn't exist in database."));
    }
    private User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new ResourceNotFoundException("User: " + login + " doesn't exist in database."));
    }

}

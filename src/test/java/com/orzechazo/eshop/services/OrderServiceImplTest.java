package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.Product;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private final static BigDecimal PRICE = new BigDecimal("13");
    public static final String ORDER_ID = "1";
    private static final String USER_LOGIN = "login";
    private static final String BASKET_ID = "basketId";
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BasketService basketService;

    @Test
    void getAllOrders() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        Order order2 = new Order();
        order2.setTotalPrice(PRICE);
        List<Order> orders = List.of(order1,order2);
        when(orderRepository.findAll()).thenReturn(orders);
        //when
        List<OrderDto> returnedDtos = orderService.getAllOrders();
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals(PRICE,returnedDtos.get(0).getTotalPrice());
        assertEquals(PRICE,returnedDtos.get(1).getTotalPrice());
    }

    @Test
    void getOrderDtoByOrderId() {
        //given
        Order order1 = new Order();
        order1.setTotalPrice(PRICE);
        order1.setOrderId(ORDER_ID);
        when(orderRepository.findByOrderId(any())).thenReturn(Optional.of(order1));
        //when
        OrderDto returnedDto = orderService.getOrderDtoByOrderId(ORDER_ID);
        //then
        assertEquals(PRICE,returnedDto.getTotalPrice());
        assertEquals(ORDER_ID,returnedDto.getOrderId());
    }
    @Test
    void getOrderByOrderIdBadRequest() {
        Exception exception = assertThrows(ResourceNotFoundException.class,()-> orderService.getOrderDtoByOrderId(ORDER_ID));
        assertEquals("Order with id: 1 doesn't exist in database.", exception.getMessage());
    }

    @Test
    void createOrder() {
        //given
        Product product = new Product();
        product.setName("productName");

        Basket basket = new Basket();
        basket.setBasketId(BASKET_ID);
        basket.setTotalPrice(PRICE);
        basket.setProducts(Map.of(product, 1));

        User user = new User();
        user.setLogin(USER_LOGIN);
        user.setBasket(basket);

        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));
        //when
        OrderDto createdDto = orderService.createOrder(USER_LOGIN);
        //then
        assertEquals(PRICE,createdDto.getTotalPrice());
        verify(userRepository, times(1)).findByLogin(anyString());
        verify(basketService,times(1)).deleteBasket(anyString());
    }

    @Test
    void createOrderWIthEmptyBasket() {
        //given
        User user = new User();
        user.setLogin(USER_LOGIN);
        user.setBasket(new Basket());
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));
        //when
        assertThrows(BadRequestException.class,()-> orderService.createOrder(USER_LOGIN));
    }

    @Test
    void deleteOrderByOrderId() {
        //given
        Order order = new Order();
        User user = new User();
        order.setUser(user);
        when(orderRepository.findByOrderId(any())).thenReturn(Optional.of(order));
        //when
        orderService.deleteOrderByOrderId(ORDER_ID);
        //then
        verify(orderRepository,times(1)).delete(any());
    }
}
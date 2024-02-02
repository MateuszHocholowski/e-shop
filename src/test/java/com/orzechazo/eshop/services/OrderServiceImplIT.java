package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.Bootstrap;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.domain.enums.OrderStatus;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.ProductRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.orzechazo.eshop.bootstrap.tests.Bootstrap.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceImplIT {


    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    private OrderServiceImpl orderService;
    private UserServiceImpl userService;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024,1,1,0,0);
    private int DB_DEFAULT_ORDER_COUNT;
    private int DB_INITIAL_USER_ORDER_COUNT;
    private Bootstrap bootstrap;
    private BasketService basketService;
    @BeforeEach
    void setUp() {
        bootstrap = new Bootstrap(orderRepository,userRepository,productRepository,basketRepository);
        bootstrap.loadData();

        basketService = new BasketServiceImpl(basketRepository,productRepository);
        userService = new UserServiceImpl(userRepository);
        orderService = new OrderServiceImpl(orderRepository, userRepository, basketService);
        DB_DEFAULT_ORDER_COUNT = bootstrap.getOrders().size();
        DB_INITIAL_USER_ORDER_COUNT = bootstrap.getUser1_orders().size();
    }

    @Test
    void getAllOrders() {
        //given
        List<String> dbOrderIdList = bootstrap.getOrders().stream()
                .map(Order::getOrderId).toList();
        //when
        List<OrderDto> orderDtos = orderService.getAllOrders();
        List<String> returnedDtosIdList = orderDtos.stream()
                .map(OrderDto::getOrderId).toList();
        //then
        assertThat(returnedDtosIdList).containsExactlyInAnyOrderElementsOf(dbOrderIdList);
    }

    @Test
    void getOrderDtoByOrderId() {
        //when
        OrderDto returnedDto = orderService.getOrderDtoByOrderId(DB_ORDER_ID1);
        //then
        assertEquals(DATE_TIME,returnedDto.getOrderDate());
        assertEquals(DATE_TIME,returnedDto.getPaymentDate());
        assertEquals(DATE_TIME,returnedDto.getAdmissionDate());
        assertEquals(DATE_TIME,returnedDto.getRealizationDate());
        assertEquals(ORDER1_TOTAL_PRICE,returnedDto.getTotalPrice());
        assertEquals(DB_USER_LOGIN,returnedDto.getUserLogin());
    }

    @Test
    void getOrderByOrderIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                ()-> orderService.getOrderDtoByOrderId("orderNotInDatabase"));
        assertEquals("Order with id: orderNotInDatabase doesn't exist in database.",exception.getMessage());
    }

    @Test
    void createOrder() {
        //given
        UserDto initialUser = userService.getUserDtoByLogin(DB_USER_LOGIN);
        Set<String> expectedProductNamesSet = initialUser.getBasket().getProductNamesMap().keySet();
        BigDecimal expectedOrderTotalPrice = initialUser.getBasket().getTotalPrice();
        //when
        OrderDto createdDto = orderService.createOrder(DB_USER_LOGIN);
        UserDto updatedUser = userService.getUserDtoByLogin(DB_USER_LOGIN);

        //then
        assertEquals(DB_USER_LOGIN, createdDto.getUserLogin());
        assertNotNull(createdDto.getOrderId());
        assertNotNull(createdDto.getOrderDate());
        assertEquals(OrderStatus.PENDING, createdDto.getOrderStatus());
        assertEquals(expectedOrderTotalPrice, createdDto.getTotalPrice());
        assertEquals(expectedProductNamesSet, createdDto.getProductNamesMap().keySet());

        assertEquals(DB_DEFAULT_ORDER_COUNT+1,orderRepository.count());
        assertEquals(DB_INITIAL_USER_ORDER_COUNT + 1, updatedUser.getOrderIdList().size());
        assertTrue(updatedUser.getBasket().getProductNamesMap().isEmpty());

        assertThrows(ResourceNotFoundException.class,
                () -> basketService.getBasketDtoByBasketId(initialUser.getBasket().getBasketId()));
    }

    @Test
    void tryToCreateOrderFromEmptyBasket() {
        Exception exception = assertThrows(BadRequestException.class,
                ()-> orderService.createOrder(EMPTY_USER));
        assertEquals("Cannot create order: basket is empty", exception.getMessage());
    }

    @Test
    void deleteOrderByOrderId() {
        //when
        orderService.deleteOrderByOrderId(DB_ORDER_ID1);
        long currentUserOrderIdListSize = userService.getUserDtoByLogin(DB_USER_LOGIN).getOrderIdList().size();
        //then
        assertEquals(DB_DEFAULT_ORDER_COUNT-1, orderRepository.count());
        assertEquals(DB_INITIAL_USER_ORDER_COUNT -1, currentUserOrderIdListSize);
    }
}
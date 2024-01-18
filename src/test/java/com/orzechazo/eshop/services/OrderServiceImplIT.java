package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapUsersAndOrders;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.OrderRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceImplIT {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    private OrderServiceImpl orderService;
    private UserServiceImpl userService;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2024,1,1,0,0);
    private static final String DB_USER_LOGIN = BootstrapUsersAndOrders.DB_USER_LOGIN;
    private static final String DB_ORDER_ID1 = BootstrapUsersAndOrders.DB_ORDER_ID1;
    private int DB_DEFAULT_ORDER_COUNT;
    private BootstrapUsersAndOrders bootstrapUsersAndOrders;
    @BeforeEach
    void setUp() {
        bootstrapUsersAndOrders = new BootstrapUsersAndOrders(orderRepository,userRepository);
        bootstrapUsersAndOrders.loadData();

        userService = new UserServiceImpl(userRepository);
        orderService = new OrderServiceImpl(orderRepository, userService);
        DB_DEFAULT_ORDER_COUNT = bootstrapUsersAndOrders.getOrders().size();
    }

    @Test
    void getAllOrders() {
        //given
        List<String> dbOrderIdList = bootstrapUsersAndOrders.getOrders().stream()
                .map(Order::getOrderId).toList();
        //when
        List<OrderDto> orderDtos = orderService.getAllOrders();
        List<String> orderDtosIdList = orderDtos.stream()
                .map(OrderDto::getOrderId).toList();
        //then
        assertThat(dbOrderIdList).containsExactlyInAnyOrderElementsOf(orderDtosIdList);
    }

    @Test
    void getOrderByOrderId() {
        //when
        OrderDto returnedDto = orderService.getOrderDtoByOrderId(DB_ORDER_ID1);
        //then
        assertEquals(DATE_TIME,returnedDto.getOrderDate());
        assertEquals(DATE_TIME,returnedDto.getPaymentDate());
        assertEquals(DATE_TIME,returnedDto.getAdmissionDate());
        assertEquals(DATE_TIME,returnedDto.getRealizationDate());
        assertEquals(new BigDecimal("100"),returnedDto.getTotalPrice());
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
        OrderDto orderDto = OrderDto.builder().userLogin(DB_USER_LOGIN)
                .totalPrice(new BigDecimal("2")).build();
        //when
        OrderDto createdDto = orderService.createOrder(orderDto);
        //then
        assertNotNull(createdDto.getOrderId());
        assertNotNull(createdDto.getOrderDate());
        assertEquals(new BigDecimal("2"),createdDto.getTotalPrice());
        assertEquals(DB_DEFAULT_ORDER_COUNT+1,orderRepository.count());
        assertEquals(DB_USER_LOGIN, createdDto.getUserLogin());
    }

    @Test
    void createOrderExistingId() {
        OrderDto orderDto = OrderDto.builder().orderId(DB_ORDER_ID1).build();
        Exception exception = assertThrows(BadRequestException.class,
                () -> orderService.createOrder(orderDto));
        assertEquals("Order already has an id: " + DB_ORDER_ID1,exception.getMessage());
    }

    @Test
    void deleteOrderByOrderId() {
        //given
        long dbUser1OrderIdListSize = bootstrapUsersAndOrders.getUser1_orders().size();
        //when
        orderService.deleteOrderByOrderId(DB_ORDER_ID1);
        long currentUser1OrderIdListSize = userService.getUserDtoByLogin(DB_USER_LOGIN).getOrderIdList().size();
        //then
        assertEquals(DB_DEFAULT_ORDER_COUNT-1, orderRepository.count());
        assertEquals(dbUser1OrderIdListSize -1, currentUser1OrderIdListSize);
    }
}
package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapUsersAndOrders;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
import com.orzechazo.eshop.services.OrderServiceImpl;
import com.orzechazo.eshop.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static com.orzechazo.eshop.bootstrap.tests.BootstrapUsersAndOrders.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderControllerE2ETest {
    public static final String ORDER_ID_NOT_IN_DB = "orderIdNotInDB";
    public static final String USER_LOGIN_NOT_IN_DB = "userNotInDb";
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    private MockMvc mockMvc;
    private int DEFAULT_DB_ORDER_SIZE;
    private int DEFAULT_DB_USER1_ORDERS_SIZE;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private BootstrapUsersAndOrders bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new BootstrapUsersAndOrders(orderRepository,userRepository);
        bootstrap.loadData();
        UserServiceImpl userService = new UserServiceImpl(userRepository);
        OrderServiceImpl orderService = new OrderServiceImpl(orderRepository, userService);
        OrderController orderController = new OrderController(orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        DEFAULT_DB_ORDER_SIZE = bootstrap.getOrders().size();
        DEFAULT_DB_USER1_ORDERS_SIZE = bootstrap.getUser1_orders().size();
    }

    @Test
    void getAllOrders() throws Exception {
        List<String> DB_ORDER_ID_LIST = bootstrap.getOrders().stream().map(Order::getOrderId).toList();
        mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(DEFAULT_DB_ORDER_SIZE)))
                .andExpect(jsonPath("$[*].orderId").value(containsInAnyOrder(DB_ORDER_ID_LIST.toArray())));
    }
    @Test
    void getOrderByOrderId() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String DB_DATE_TIME = BootstrapUsersAndOrders.DATE_TIME.format(formatter);

        mockMvc.perform(get("/orders/" + DB_ORDER_ID1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId",equalTo(DB_ORDER_ID1)))
                .andExpect(jsonPath("$.orderDate").value(DB_DATE_TIME))
                .andExpect(jsonPath("$.admissionDate",equalTo(DB_DATE_TIME)))
                .andExpect(jsonPath("$.realizationDate", equalTo(DB_DATE_TIME)))
                .andExpect(jsonPath("$.paymentDate",equalTo(DB_DATE_TIME)))
                .andExpect(jsonPath("$.totalPrice",is(ORDER1_TOTAL_PRICE.intValue())));
    }

    @Test
    void getOrderByOrderIdThatIsNotInDB() throws Exception{
        mockMvc.perform(get("/orders/" + ORDER_ID_NOT_IN_DB)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Order with id: " + ORDER_ID_NOT_IN_DB + " doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createOrder() throws Exception{
        OrderDto newOrder = OrderDto.builder().userLogin(DB_USER_LOGIN).build();
        mockMvc.perform(put("/orders/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId",notNullValue()))
                .andExpect(jsonPath("$.orderDate",notNullValue()))
                .andExpect(jsonPath("$.userLogin",equalTo(DB_USER_LOGIN)));

                assertEquals(DEFAULT_DB_ORDER_SIZE+1, orderRepository.count());
                assertEquals(DEFAULT_DB_USER1_ORDERS_SIZE +1, bootstrap.getUser1_orders().size());
    }

    @Test
    void testCreateOrderForUserThatIsNotInDB() throws Exception {
        OrderDto newOrder = OrderDto.builder().userLogin(USER_LOGIN_NOT_IN_DB).build();
        mockMvc.perform(put("/orders/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newOrder)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("User:"+ USER_LOGIN_NOT_IN_DB + " doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteOrderByOrderId() throws Exception{
        mockMvc.perform(delete("/orders/delete/" + DB_ORDER_ID1))
                .andExpect(status().isOk());

        assertEquals(DEFAULT_DB_ORDER_SIZE-1, orderRepository.count());
        assertEquals(DEFAULT_DB_USER1_ORDERS_SIZE -1, bootstrap.getUser1_orders().size());
    }
}
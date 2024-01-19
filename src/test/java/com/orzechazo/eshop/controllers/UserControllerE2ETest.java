package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapUsersAndOrders;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.OrderRepository;
import com.orzechazo.eshop.repositories.UserRepository;
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

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerE2ETest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    MockMvc mockMvc;
    private final static String DB_USER_LOGIN = BootstrapUsersAndOrders.DB_USER_LOGIN;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private static int DEFAULT_DB_USER_COUNT;
    private List<String> DB_USER1_ORDER_ID_LIST;
    BootstrapUsersAndOrders bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new BootstrapUsersAndOrders(orderRepository, userRepository);
        bootstrap.loadData();

        UserServiceImpl userService = new UserServiceImpl(userRepository);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        DEFAULT_DB_USER_COUNT = bootstrap.getUsers().size();
        DB_USER1_ORDER_ID_LIST = bootstrap.getUser1_orders().stream().map(Order::getOrderId).toList();
    }

    @Test
    void getAllUsers() throws Exception {
        List<String> DB_USERS_LOGINS_LIST = bootstrap.getUsers().stream().map(User::getLogin).toList();
        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(DEFAULT_DB_USER_COUNT)))
                .andExpect(jsonPath("$[*].login").value(containsInAnyOrder(DB_USERS_LOGINS_LIST.toArray())));
    }

    @Test
    void getUserByLogin() throws Exception{
        mockMvc.perform(get("/users/" + DB_USER_LOGIN)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login",equalTo(DB_USER_LOGIN)))
                .andExpect(jsonPath("$.password",nullValue()))
                .andExpect(jsonPath("$.orderIdList")
                        .value(containsInAnyOrder(DB_USER1_ORDER_ID_LIST.toArray())));
    }

    @Test
    void getUserByLoginNotFound() throws Exception {
        mockMvc.perform(get("/users/userNotInDb")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("User: userNotInDb doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getOrdersByUser() throws Exception {
        mockMvc.perform(get("/users/" + DB_USER_LOGIN + "/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].orderId")
                        .value(containsInAnyOrder(DB_USER1_ORDER_ID_LIST.toArray())));
    }

    @Test
    void createNewUser() throws Exception {
        UserDto newUser = UserDto.builder().login("newUser")
                .password("testPassword").build();
        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login",equalTo("newUser")))
                .andExpect(jsonPath("$.password",nullValue()));

        assertEquals(DEFAULT_DB_USER_COUNT+1,userRepository.count());
    }

    @Test
    void testCreateNewUserTwice() throws Exception{
        UserDto newUser = UserDto.builder().login("newUser")
                .password("testPassword").build();
        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("User: newUser is already in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createNewUserExists() throws Exception{
        UserDto existingUser = UserDto.builder().login(DB_USER_LOGIN).build();
        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(existingUser)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("User: "+ DB_USER_LOGIN + " is already in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = UserDto.builder().login(DB_USER_LOGIN).build();
        mockMvc.perform(post("/users/"+ DB_USER_LOGIN + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto))
                .param("login",DB_USER_LOGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login",equalTo(DB_USER_LOGIN)))
                .andExpect(jsonPath("$.password",nullValue()));

        assertEquals(DEFAULT_DB_USER_COUNT,userRepository.count());
    }

    @Test
    void updateUserNotFound() throws Exception {
        UserDto userDto = UserDto.builder().login("userNotInDb").build();
        mockMvc.perform(post("/users/test/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto))
                .param("login","userNotInDb"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("User: userNotInDb doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/"+ DB_USER_LOGIN + "/delete"))
                .andExpect(status().isOk());

        assertEquals(DEFAULT_DB_USER_COUNT-1,userRepository.count());
    }
}
package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.Bootstrap;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
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

import java.util.List;

import static com.orzechazo.eshop.bootstrap.tests.Bootstrap.DB_USER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplIT {
    @Autowired
    private BasketRepository basketRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    private UserServiceImpl userService;
    private int DEFAULT_DB_USER_COUNT;
    private Bootstrap bootstrap;
    @BeforeEach
    void setUp() {
        bootstrap = new Bootstrap(orderRepository, userRepository, productRepository, basketRepository);
        bootstrap.loadData();
        DEFAULT_DB_USER_COUNT = bootstrap.getUsers().size();

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        List<String> expectedUsersLoginList = bootstrap.getUsers().stream()
                .map(User::getLogin).toList();
        //when
        List<UserDto> userDtos = userService.getAllUsers();
        List<String> userDtosLogins = userDtos.stream().map(UserDto::getLogin).toList();
        //then
        assertThat(expectedUsersLoginList).containsExactlyInAnyOrderElementsOf(userDtosLogins);
    }

    @Test
    void getUserDtoByLogin() {
        UserDto returnedDto = userService.getUserDtoByLogin(DB_USER_LOGIN);

        assertEquals(DB_USER_LOGIN,returnedDto.getLogin());
    }
    @Test
    void shouldNotMapUsersPassword() {
        UserDto returnedDto = userService.getUserDtoByLogin(DB_USER_LOGIN);

        assertNull(returnedDto.getPassword());
    }

    @Test
    void tryToGetUserByLoginNotExistingInDatabase() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserDtoByLogin("notExistingUser"));
        assertEquals("User: notExistingUser doesn't exist in database.",exception.getMessage());
    }

    @Test
    void getOrdersByUser() {
        //given
        List<String> expectedUsersOrderIdList = bootstrap.getUser1_orders().stream()
                .map(Order::getOrderId).toList();
        //when
        List<OrderDto> returnedUsersOrders = userService.getOrdersByUser(DB_USER_LOGIN);
        List<String> returnedUsersOrderIdList = returnedUsersOrders.stream()
                .map(OrderDto::getOrderId).toList();
        //then
        assertThat(expectedUsersOrderIdList)
                .containsExactlyInAnyOrderElementsOf(returnedUsersOrderIdList);
    }

    @Test
    void createUser() {
        UserDto newUser = UserDto.builder().login("newUser")
                .password("testPassword").build();
        //when
        UserDto createdDto = userService.createUser(newUser);
        //then
        assertEquals("newUser",createdDto.getLogin());
        assertNull(createdDto.getPassword());
        assertNotNull(createdDto.getBasket().getBasketId());
        assertEquals(DEFAULT_DB_USER_COUNT + 1,userRepository.count());
    }

    @Test
    void testTryToCreateUserWhoseLoginIsAlreadyInDatabase() {
        UserDto userDto = UserDto.builder().login(DB_USER_LOGIN).password("testPassword").build();

        Exception exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(userDto));
        assertEquals("User: " + DB_USER_LOGIN + " is already in database.",exception.getMessage());
    }

    @Test
    void tryToCreateTheSameUserTwice() {
        UserDto userDto = UserDto.builder().login("newUser").build();

        userService.createUser(userDto);
        Exception exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(userDto));
        assertEquals("User: newUser is already in database.",exception.getMessage());
    }

    @Test
    void updateUser() {
        UserDto userToUpdate = UserDto.builder().login(DB_USER_LOGIN)
                .password("newPassword").build();
        //when
        userService.updateUser(userToUpdate);
        UserDto updatedUser = userService.getUserDtoByLogin(DB_USER_LOGIN);
        //then
        assertEquals(DB_USER_LOGIN,updatedUser.getLogin());
        assertEquals(DEFAULT_DB_USER_COUNT,userRepository.count());
        //todo implement methods to update user's Data and change the test
    }

    @Test
    void tryToUpdateUserWithLoginNotInDatabase() {
        UserDto userToUpdate = UserDto.builder().login("userNotInDatabase").
                password("newPassword").build();
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(userToUpdate));
        assertEquals("User: userNotInDatabase doesn't exist in database.",exception.getMessage());
    }

    @Test
    void deleteUserByLogin() {
        //when
        userService.deleteUserByLogin(DB_USER_LOGIN);
        //then
        assertEquals(DEFAULT_DB_USER_COUNT -1,userRepository.count());
    }
}

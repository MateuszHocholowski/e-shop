package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapUser;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplIT {
    @Autowired
    private UserRepository userRepository;
    private UserServiceImpl userService;
    private final static String DB_USER_NAME = BootstrapUser.DB_USER_NAME;
    private int DEFAULT_DB_USER_COUNT;
    private BootstrapUser bootstrapUser;
    @BeforeEach
    void setUp() {
        bootstrapUser = new BootstrapUser(userRepository);
        bootstrapUser.loadData();
        DEFAULT_DB_USER_COUNT = bootstrapUser.getUsers().size();

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        List<String> dbUsersLoginList = bootstrapUser.getUsers().stream()
                .map(User::getLogin).toList();
        //when
        List<UserDto> userDtos = userService.getAllUsers();
        List<String> userDtosLogins = userDtos.stream().map(UserDto::getLogin).toList();
        //then
        assertThat(dbUsersLoginList).containsExactlyInAnyOrderElementsOf(userDtosLogins);
    }

    @Test
    void getUserByLogin() {
        UserDto returnedDto = userService.getUserDtoByLogin(DB_USER_NAME);

        assertEquals("user1",returnedDto.getLogin());
        assertNull(returnedDto.getPassword());
    }

    @Test
    void getUserByLoginNotExistingInDatabase() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserDtoByLogin("notExistingUser"));
        assertEquals("User: notExistingUser doesn't exist in database.",exception.getMessage());
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
        assertEquals(DEFAULT_DB_USER_COUNT + 1,userRepository.count());
    }

    @Test
    void testTryToCreateUserWhoseLoginIsAlreadyInDatabase() {
        UserDto userDto = UserDto.builder().login(DB_USER_NAME).password("testPassword").build();

        Exception exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(userDto));
        assertEquals("User: " + DB_USER_NAME + " is already in database.",exception.getMessage());
    }

    @Test
    void testTryToCreateTheSameUserTwice() {
        UserDto userDto = UserDto.builder().login("newUser").build();

        userService.createUser(userDto);
        Exception exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(userDto));
        assertEquals("User: newUser is already in database.",exception.getMessage());
    }

    @Test
    void updateUser() {
        OrderDto newOrder = OrderDto.builder().orderId("1").build();
        UserDto userToUpdate = UserDto.builder().login(DB_USER_NAME).orders(List.of(newOrder))
                .password("newPassword").build();
        //when
        userService.updateUser(userToUpdate);
        UserDto updatedUser = userService.getUserDtoByLogin(DB_USER_NAME);
        //then
        assertEquals("user1",updatedUser.getLogin());
        assertEquals("1", updatedUser.getOrders().get(0).getOrderId());
        assertEquals(DEFAULT_DB_USER_COUNT,userRepository.count());
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
        userService.deleteUserByLogin(DB_USER_NAME);
        //then
        assertEquals(DEFAULT_DB_USER_COUNT -1,userRepository.count());
    }
}
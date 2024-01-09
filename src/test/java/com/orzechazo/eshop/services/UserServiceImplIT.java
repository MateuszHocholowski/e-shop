package com.orzechazo.eshop.services;

import com.orzechazo.eshop.bootstrap.tests.BootstrapUser;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceImplIT {

    @Autowired
    private UserRepository userRepository;
    private UserServiceImpl userService;
    @BeforeEach
    void setUp() {
        BootstrapUser bootstrapUser = new BootstrapUser(userRepository);
        bootstrapUser.loadData();

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        //given
        long count = userRepository.count();
        //when
        List<UserDto> userDtos = userService.getAllUsers();
        //then
        assertEquals(count,userDtos.size());
        assertEquals("user1",userDtos.get(0).getLogin());
        assertEquals("user2", userDtos.get(1).getLogin());
    }

    @Test
    void getUserByLogin() {
        UserDto returnedDto = userService.getUserDtoByLogin("user1");

        assertEquals("user1",returnedDto.getLogin());
        assertNull(returnedDto.getPassword());
    }

    @Test
    void getUserByLoginNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserDtoByLogin("test"));
        assertEquals("User: test doesn't exist in database.",exception.getMessage());
    }

    @Test
    void createUser() {
        long count = userRepository.count();
        UserDto userDto = UserDto.builder().login("testLogin")
                .password("testPassword").build();
        //when
        UserDto createdDto = userService.createUser(userDto);
        //then
        assertEquals("testLogin",createdDto.getLogin());
        assertNull(createdDto.getPassword());
        assertEquals(count + 1,userRepository.count());
    }

    @Test
    void createUserExists() {
        UserDto userDto = UserDto.builder().login("user2").password("testPassword").build();

        Exception exception = assertThrows(BadRequestException.class,
                () -> userService.createUser(userDto));
        assertEquals("User: user2 is already in database.",exception.getMessage());
    }

    @Test
    void updateUser() {
        long count = userRepository.count();
        UserDto userToUpdate = UserDto.builder().login("user1").
                password("newPassword").build();
        //when
        UserDto updatedUser = userService.updateUser(userToUpdate);
        //then
        assertEquals("user1",updatedUser.getLogin());
        assertEquals(count,userRepository.count());
    }

    @Test
    void updateUserNotExist() {
        UserDto userToUpdate = UserDto.builder().login("test").
                password("newPassword").build();
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(userToUpdate));
        assertEquals("User: test doesn't exist in database.",exception.getMessage());
    }


    @Test
    void deleteUserByLogin() {
        long count = userRepository.count();
        //when
        userService.deleteUserByLogin("user1");
        //then
        assertEquals(count -1,userRepository.count());
    }
}
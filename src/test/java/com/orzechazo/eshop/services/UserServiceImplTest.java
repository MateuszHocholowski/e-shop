package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        //given
        User user1 = new User();
        user1.setLogin("login1");
        user1.setPassword("password1");
        User user2 = new User();
        user2.setLogin("login2");
        user2.setPassword("password2");
        List<User> users = List.of(user1,user2);
        when(userRepository.findAll()).thenReturn(users);
        //when
        List<UserDto> returnedDtos = userService.getAllUsers();
        //then
        assertEquals(2,returnedDtos.size());
        assertEquals("login1",returnedDtos.get(0).getLogin());
        assertNull(returnedDtos.get(1).getPassword());
    }

    @Test
    void getUserByLogin() {
        //given
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("login1");
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user1));
        //when
        UserDto returnedDto = userService.getUserByLogin("login1");
        //then
        assertEquals("login1",returnedDto.getLogin());
    }

    @Test
    void getUserByLoginNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> userService.getUserByLogin("test"));
        assertEquals("User: test doesn't exist in database",exception.getMessage());
    }

    @Test
    void createUser() {
        //given
        UserDto userDto = UserDto.builder().login("login1").build();
        User user = new User();
        user.setId(1L);
        user.setLogin("login1");
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto createdDto = userService.createUser(userDto);
        //then
        assertEquals("login1",createdDto.getLogin());
        verify(userRepository,times(1)).save(any());
    }
    @Test
    void createUserBadRequest() {
        //given
        UserDto userDto = UserDto.builder().login("login1").build();
        User user = new User();
        user.setId(1L);
        user.setLogin("login1");
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
        //when
        Exception exception = assertThrows(BadRequestException.class,() -> userService.createUser(userDto));
        //then
        assertEquals("User: login1 is already in database.",exception.getMessage());
        verify(userRepository,times(0)).save(any());
    }
    @Test
    void updateUser() {
        //given
        UserDto userDto = UserDto.builder().login("login1").build();
        User user = new User();
        user.setId(1L);
        user.setLogin("login1");
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto createdDto = userService.updateUser("login1",userDto);
        //then
        assertEquals("login1",createdDto.getLogin());
        verify(userRepository,times(1)).save(any());
    }

    @Test
    void deleteUserById() {
        //when
        userService.deleteUserByLogin("login");
        //then
        verify(userRepository,times(1)).deleteByLogin(any());
    }
}
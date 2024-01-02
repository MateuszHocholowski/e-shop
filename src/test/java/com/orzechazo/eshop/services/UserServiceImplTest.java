package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.mappers.UserMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    void getAllUsers() {
        //given
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("login1");
        user1.setPassword("password1");
        User user2 = new User();
        user2.setId(2L);
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
    void getUserById() {
        //given
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("login1");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        //when
        UserDto returnedDto = userService.getUserById(1L);
        //then
        assertEquals(1L,user1.getId());
        assertEquals("login1",returnedDto.getLogin());
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
        assertEquals(1L,returnedDto.getId());
    }

    @Test
    void createUser() {
        //given
        UserDto userDto = UserDto.builder().id(1L).login("login1").build();
        User user = new User();
        user.setId(1L);
        user.setLogin("login1");
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto createdDto = userService.createUser(userDto);
        //then
        assertEquals(1L,createdDto.getId());
        assertEquals("login1",createdDto.getLogin());
        verify(userRepository,times(1)).save(any());
    }
    @Test
    void createUserExistingLogin() {
        //given
        UserDto userDto = UserDto.builder().id(1L).login("login1").build();
        User user = new User();
        user.setId(1L);
        user.setLogin("login1");
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
        //when
        UserDto createdDto = userService.createUser(userDto);
        //then
        assertNull(createdDto);
        verify(userRepository,times(0)).save(any());
    }
    @Test
    void updateUser() {
        //given
        UserDto userDto = UserDto.builder().id(1L).login("login1").build();
        User user = new User();
        user.setId(1L);
        user.setLogin("login1");
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto createdDto = userService.updateUser(1L,userDto);
        //then
        assertEquals(1L,createdDto.getId());
        assertEquals("login1",createdDto.getLogin());
        verify(userRepository,times(1)).save(any());
    }

    @Test
    void deleteUserById() {
        //when
        userService.deleteUserById(1L);
        //then
        verify(userRepository,times(1)).deleteById(anyLong());
    }
}
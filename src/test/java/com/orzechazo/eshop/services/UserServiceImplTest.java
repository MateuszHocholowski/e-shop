package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.OrderDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String USER_LOGIN = "login1";
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void getAllUsers() {
        //given
        User user1 = new User();
        user1.setLogin(USER_LOGIN);
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
    }

    @Test
    void getUserDtoByLogin() {
        //given
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin(USER_LOGIN);
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user1));
        //when
        UserDto returnedDto = userService.getUserDtoByLogin(USER_LOGIN);
        //then
        assertEquals(USER_LOGIN,returnedDto.getLogin());
    }

    @Test
    void getUserByLoginNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> userService.getUserDtoByLogin("test"));
        assertEquals("User: test doesn't exist in database.",exception.getMessage());
    }
    @Test
    void getOrdersByUser() {
        //given
        Order order1 = new Order();
        order1.setOrderId("order1");

        Order order2 = new Order();
        order2.setOrderId("order2");

        User user = new User();
        user.setLogin(USER_LOGIN);
        user.setOrders(List.of(order1,order2));

        List<String> orderIds = user.getOrders().stream().map(Order::getOrderId).toList();
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));
        //when
        List<OrderDto> returnedDtos = userService.getOrdersByUser(USER_LOGIN);
        List<String> returnedDtosOrderIds = returnedDtos.stream().map(OrderDto::getOrderId).toList();
        //then
        assertEquals(2,returnedDtos.size());
        assertThat(returnedDtosOrderIds).containsExactlyInAnyOrderElementsOf(orderIds);
    }

    @Test
    void createUser() {
        //given
        UserDto userDto = UserDto.builder().login(USER_LOGIN).build();
        User user = new User();
        user.setId(1L);
        user.setLogin(USER_LOGIN);
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto createdDto = userService.createUser(userDto);
        //then
        assertEquals(USER_LOGIN,createdDto.getLogin());
        verify(userRepository,times(1)).save(any());
    }
    @Test
    void createUserBadRequest() {
        //given
        UserDto userDto = UserDto.builder().login(USER_LOGIN).build();
        User user = new User();
        user.setId(1L);
        user.setLogin(USER_LOGIN);
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
        //when
        Exception exception = assertThrows(BadRequestException.class,() -> userService.createUser(userDto));
        //then
        assertEquals("User: " + USER_LOGIN + " is already in database.",exception.getMessage());
        verify(userRepository,times(0)).save(any());
    }
    @Test
    void updateUser() {
        //given
        BasketDto newBasket = BasketDto.builder().basketId("newBasket").build();
        UserDto userDto = UserDto.builder().login(USER_LOGIN).basket(newBasket).build();
        User user = new User();
        user.setLogin(USER_LOGIN);
        when(userRepository.findByLogin(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        //when
        UserDto updatedDto = userService.updateUser(userDto);
        //then
        assertEquals(USER_LOGIN,updatedDto.getLogin());
        assertEquals("newBasket",updatedDto.getBasket().getBasketId());
        verify(userRepository,times(1)).save(any());
    }
    @Test
    void updateUserBadRequest() {
        //given
        UserDto userDto = UserDto.builder().login(USER_LOGIN).build();
        //when
        Exception exception = assertThrows(ResourceNotFoundException.class,() -> userService.updateUser(userDto));
        //then
        assertEquals("User: " + USER_LOGIN + " doesn't exist in database.",exception.getMessage());
        verify(userRepository,times(0)).save(any());
    }

    @Test
    void deleteUserByLogin() {
        //when
        userService.deleteUserByLogin(USER_LOGIN);
        //then
        verify(userRepository,times(1)).deleteByLogin(any());
    }
}
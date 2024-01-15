package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserDtoByLogin(String login);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    Order addOrder(String userLogin, Order order);
    void deleteUserByLogin(String login);

}

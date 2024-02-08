package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.OrderDto;
import com.orzechazo.eshop.domain.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserDtoByLogin(String login);
    List<OrderDto> getOrdersByUser(String userLogin);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    void deleteUserByLogin(String login);

}

package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByLogin(String login);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    void deleteUserByLogin(String login);

}

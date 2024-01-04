package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.dto.UserDto;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserByLogin(String login) throws BadRequestException;
    UserDto createUser(UserDto userDto) throws BadRequestException;
    UserDto updateUser(UserDto userDto);
    void deleteUserByLogin(String login);

}

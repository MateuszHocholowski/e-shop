package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.mappers.UserMapper;
import com.orzechazo.eshop.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .toList();
    }
    @Override
    public UserDto getUserByLogin(String login) {
        return userMapper.userToUserDto(userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + login + " doesn't exist in database")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userMapper.userDtoToUser(userDto);
        if (userRepository.findByLogin(newUser.getLogin()).isEmpty()) {
            return saveUserAndReturnDto(newUser);
        } else {
            throw new BadRequestException("User: " + userDto.getLogin() + " is already in database.");
        }
    }

    @Override
    public UserDto updateUser(String login, UserDto userDto) {
        User updateUser = userMapper.userDtoToUser(userDto);
        updateUser.setLogin(login);
        return saveUserAndReturnDto(updateUser);
    }

    private UserDto saveUserAndReturnDto(User user) {
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUserByLogin(String login) {
        userRepository.deleteByLogin(login);
    }
}

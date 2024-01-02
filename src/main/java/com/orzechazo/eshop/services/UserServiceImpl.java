package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.UserDto;
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

        return null;
    }

    @Override
    public UserDto getUserById(Long id) {
        return null;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        return null;
    }

    @Override
    public UserDto saveUserAndReturnDto(User user) {
        return null;
    }

    @Override
    public void deleteUserById(Long id) {

    }
}

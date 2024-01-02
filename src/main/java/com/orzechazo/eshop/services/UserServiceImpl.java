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
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id).orElseThrow(RuntimeException::new));
    }

    @Override
    public UserDto getUserByLogin(String login) {
        return userMapper.userToUserDto(userRepository.findByLogin(login).orElseThrow(RuntimeException::new));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userMapper.userDtoToUser(userDto);
        if (userRepository.findByLogin(newUser.getLogin()).isEmpty()) {
            return saveUserAndReturnDto(newUser);
        } else {
            System.out.println("User with that Login already exists");
        }
        return null;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        return saveUserAndReturnDto(userMapper.userDtoToUser(userDto));
    }

    @Override
    public UserDto saveUserAndReturnDto(User user) {
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}

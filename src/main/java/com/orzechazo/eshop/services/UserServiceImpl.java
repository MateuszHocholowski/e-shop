package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Order;
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
    public UserDto getUserDtoByLogin(String login) {
        return userMapper.userToUserDto(getUserByLogin(login));
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
    public UserDto updateUser(UserDto userDto) {
        User currentUser = getUserByLogin(userDto.getLogin());
        if (userDto.getPassword() != null && !userDto.getPassword().equals(currentUser.getPassword())) {
            currentUser.setPassword(userDto.getPassword());
        }
        return saveUserAndReturnDto(currentUser);
    }

    @Override
    public Order addOrder(String userLogin, Order order) {
        User currentUser = getUserByLogin(userLogin);
        currentUser.addOrder(order);
        return order;
    }

    @Override
    public void deleteOrder(Order order) {
        User currentUser = getUserByLogin(order.getUser().getLogin());
        currentUser.getOrders().remove(order);
        saveUserAndReturnDto(currentUser);
    }

    private UserDto saveUserAndReturnDto(User user) {
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUserByLogin(String login) {
        userRepository.deleteByLogin(login);
    }

    private User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new ResourceNotFoundException("User: " + login + " doesn't exist in database."));
    }
}

package com.orzechazo.eshop.services;

import com.orzechazo.eshop.mappers.UserMapper;
import com.orzechazo.eshop.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

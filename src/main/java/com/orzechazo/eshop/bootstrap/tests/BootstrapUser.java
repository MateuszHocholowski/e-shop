package com.orzechazo.eshop.bootstrap.tests;

import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BootstrapUser {

    private final UserRepository userRepository;

    public BootstrapUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loadData() {
        setUserData();
    }
    private void setUserData() {
        log.debug("Loading User Data");

        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("password1");
        userRepository.save(user1);

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password2");
        userRepository.save(user2);

        log.debug("Users loaded: " + userRepository.count());
    }
}


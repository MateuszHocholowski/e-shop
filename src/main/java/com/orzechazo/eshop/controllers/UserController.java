package com.orzechazo.eshop.controllers;

import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{login}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByLogin(@PathVariable String login) {
        return userService.getUserByLogin(login);
    }
    @PutMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createNewUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }
    @PostMapping("/{login}/update")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestParam String login, @RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }
    @DeleteMapping("/{login}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String login) {
        userService.deleteUserByLogin(login);
    }
}

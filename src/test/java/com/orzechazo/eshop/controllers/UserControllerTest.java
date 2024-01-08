package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static  org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    private MockMvc mockMvc;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsers() throws Exception{
        List<UserDto> userDtos = List.of(UserDto.builder().build(), UserDto.builder().build());
        when(userService.getAllUsers()).thenReturn(userDtos);

        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    void getUserByLogin() throws Exception {
        UserDto userDto = UserDto.builder().login("test").build();
        when(userService.getUserByLogin(anyString())).thenReturn(userDto);

        mockMvc.perform(get("/users/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login",equalTo("test")));
    }

    @Test
    void createNewUser() throws Exception {
        UserDto userDto = UserDto.builder().login("test").build();
        when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login",equalTo("test")));
    }

    @Test
    void updateUser() throws Exception{
        UserDto userDto = UserDto.builder().login("test").build();
        when(userService.updateUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users/test/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login",equalTo("test")));
    }

    @Test
    void deleteUser() throws Exception{
        mockMvc.perform(delete("/users/test/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
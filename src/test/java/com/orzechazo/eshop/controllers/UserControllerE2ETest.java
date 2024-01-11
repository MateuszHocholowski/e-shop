package com.orzechazo.eshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.orzechazo.eshop.bootstrap.tests.BootstrapUser;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.UserRepository;
import com.orzechazo.eshop.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerE2ETest {

    @Autowired
    private UserRepository userRepository;
    MockMvc mockMvc;
    private long REPO_SIZE;
    private final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @BeforeEach
    void setUp() {
        BootstrapUser bootstrapUser = new BootstrapUser(userRepository);
        bootstrapUser.loadData();

        UserServiceImpl userService = new UserServiceImpl(userRepository);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        REPO_SIZE = userRepository.count();
    }

    @Test
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize((int) REPO_SIZE)));
    }

    @Test
    void getUserByLogin() throws Exception{
        mockMvc.perform(get("/users/user1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login",equalTo("user1")))
                .andExpect(jsonPath("$.password",nullValue()));
    }

    @Test
    void getUserByLoginNotFound() throws Exception {
        mockMvc.perform(get("/users/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("User: test doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createNewUser() throws Exception {
        UserDto userDto = UserDto.builder().login("testLogin")
                .password("testPassword").build();
        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login",equalTo("testLogin")))
                .andExpect(jsonPath("$.password",nullValue()))
                .andExpect(result -> assertEquals(REPO_SIZE+1,userRepository.count()));
    }

    @Test
    void createNewUserExists() throws Exception{
        UserDto userDto = UserDto.builder().login("user2").build();
        mockMvc.perform(put("/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("User: user2 is already in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = UserDto.builder().login("user1").build();
        mockMvc.perform(post("/users/user1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto))
                .param("login","user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login",equalTo("user1")))
                .andExpect(jsonPath("$.password",nullValue()))
                .andExpect(result -> assertEquals(REPO_SIZE,userRepository.count()));
    }

    @Test
    void updateUserNotFound() throws Exception {
        UserDto userDto = UserDto.builder().login("test").build();
        mockMvc.perform(post("/users/test/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(writer.writeValueAsString(userDto))
                .param("login","test"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("User: test doesn't exist in database.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/user1/delete"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(REPO_SIZE-1,userRepository.count()));
    }
}
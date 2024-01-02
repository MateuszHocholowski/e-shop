package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void userDtoToUser() {
        User expectedUser = new User();
        expectedUser.setBasket(new Basket());
        expectedUser.setId(1L);
        expectedUser.setPassword("password");
        expectedUser.setLogin("login");
        expectedUser.setOrders(new ArrayList<>());
        expectedUser.setFavouriteProducts(new ArrayList<>());

        UserDto userDto = UserDto.builder()
                .id(1L)
                .password("password")
                .login("login")
                .orders(new ArrayList<>())
                .favouriteProducts(new ArrayList<>())
                .basket(BasketDto.builder().build())
                .build();
        //when
        User mappedUser = mapper.userDtoToUser(userDto);
        //then
        assertEquals(expectedUser,mappedUser);
    }

    @Test
    void userDtoToUserNotEquals() {
        User expectedUser = new User();
        expectedUser.setBasket(new Basket());
        expectedUser.setId(1L);
        expectedUser.setPassword("password");
        expectedUser.setLogin("login");
        expectedUser.setOrders(new ArrayList<>());
        expectedUser.setFavouriteProducts(new ArrayList<>());

        UserDto userDto = UserDto.builder()
                .id(1L)
                .password("password2")
                .login("login2")
                .orders(new ArrayList<>())
                .favouriteProducts(new ArrayList<>())
                .basket(BasketDto.builder().build())
                .build();
        //when
        User mappedUser = mapper.userDtoToUser(userDto);
        //then
        assertNotEquals(expectedUser,mappedUser);
    }
    @Test
    void userToUserDto() {
        //given
        User user = new User();
        user.setBasket(new Basket());
        user.setId(1L);
        user.setPassword("password");
        user.setLogin("login");
        user.setOrders(new ArrayList<>());
        user.setFavouriteProducts(new ArrayList<>());

        UserDto expectedDto = UserDto.builder()
                .id(1L)
                .login("login")
                .orders(new ArrayList<>())
                .favouriteProducts(new ArrayList<>())
                .basket(BasketDto.builder().build())
                .build();
        //when
        UserDto mappedDto = mapper.userToUserDto(user);

        assertEquals(expectedDto,mappedDto);
        assertNull(mappedDto.getPassword());
    }
    @Test
    void userToUserDtoNotEquals() {
        //given
        User user = new User();
        user.setBasket(new Basket());
        user.setId(1L);
        user.setPassword("password");
        user.setLogin("login");
        user.setOrders(new ArrayList<>());
        user.setFavouriteProducts(new ArrayList<>());

        UserDto expectedDto = UserDto.builder()
                .id(1L)
                .login("login2")
                .orders(new ArrayList<>())
                .favouriteProducts(new ArrayList<>())
                .basket(BasketDto.builder().build())
                .build();
        //when
        UserDto mappedDto = mapper.userToUserDto(user);

        assertNotEquals(expectedDto,mappedDto);
        assertNull(mappedDto.getPassword());
    }
    @Test
    void userDtoToUserNull() {
        //when
        User mappedUser = mapper.userDtoToUser(null);
        //then
        assertNull(mappedUser);
    }

    @Test
    void userToUserDtoNull() {
        //when
        UserDto mappedDto = mapper.userToUserDto(null);
        //then
        assertNull(mappedDto);
    }
}
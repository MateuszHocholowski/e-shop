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
        User user = new User();
        user.setBasket(new Basket());
        user.setId(1L);
        user.setPassword("password");
        user.setLogin("login");
        user.setOrders(new ArrayList<>());
        user.setFavouriteProducts(new ArrayList<>());

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
        assertEquals(user,mappedUser);
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

        UserDto userDto = UserDto.builder()
                .id(1L)
                .login("login")
                .orders(new ArrayList<>())
                .favouriteProducts(new ArrayList<>())
                .basket(BasketDto.builder().build())
                .build();
        //when
        UserDto mappedDto = mapper.userToUserDto(user);

        assertEquals(userDto,mappedDto);
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
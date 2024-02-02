package com.orzechazo.eshop.mappers;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.Order;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void userDtoToUser() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setPassword("password");
        expectedUser.setLogin("login");

        UserDto userDto = UserDto.builder()
                .password("password")
                .login("login")
                .favouriteProducts(new ArrayList<>())
                .build();
        //when
        User mappedUser = mapper.userDtoToUser(userDto);
        //then
        assertEquals(expectedUser,mappedUser);
        assertNull(mappedUser.getFavouriteProducts());
        assertNull(mappedUser.getBasket());
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
                .password("password2")
                .login("login2")
                .orderIdList(new ArrayList<>())
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
                .login("login")
                .orderIdList(new ArrayList<>())
                .favouriteProducts(new ArrayList<>())
                .basket(BasketDto.builder().productNamesMap(new HashMap<>()).build())
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
                .login("login2")
                .orderIdList(new ArrayList<>())
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

    @Test
    void ordersToOrderIdList() {
        //given
        Order order1 = new Order();
        String orderId1 = "order1";
        order1.setOrderId(orderId1);

        Order order2 = new Order();
        String orderId2 = "order2";
        order2.setOrderId(orderId2);

        Order order3 = new Order();
        String orderId3 = "order3";
        order3.setOrderId(orderId3);

        User user = new User();
        user.setOrders(List.of(order1,order2,order3));
        List<String> ordersNameList = List.of(orderId1,orderId2,orderId3);
        //when
        UserDto mappedDto = mapper.userToUserDto(user);
        //then
        assertThat(ordersNameList).containsExactlyInAnyOrderElementsOf(mappedDto.getOrderIdList());
    }
}
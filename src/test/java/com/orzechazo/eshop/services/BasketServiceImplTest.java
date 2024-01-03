package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.User;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.domain.dto.UserDto;
import com.orzechazo.eshop.exceptions.ResourceNotFoundException;
import com.orzechazo.eshop.repositories.BasketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    @InjectMocks
    private BasketServiceImpl basketService;
    @Mock
    private BasketRepository basketRepository;
    @Test
    void getBasketByBasketId() {
        //given
        Basket basket = new Basket();
        basket.setBasketId(1L);
        when(basketRepository.findByBasketId(any())).thenReturn(Optional.of(basket));
        //when
        BasketDto returnedDto = basketService.getBasketByBasketId(1L);
        //then
        assertEquals(1L,returnedDto.getBasketId());
    }

    @Test
    void getBasketByBasketIdNotFound() {
        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> basketService.getBasketByBasketId(11L));
        //then
        assertEquals("Basket: 11 doesn't exist in database",exception.getMessage());
    }
    @Test
    void getBasketByUser() {
        Basket basket = new Basket();
        basket.setBasketId(1L);
        User user = new User();
        user.setLogin("login");
        user.setBasket(basket);
        basket.setUser(user);
        UserDto userDto = UserDto.builder().login("login").build();
        when(basketRepository.findByUser(any())).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto returnedDto = basketService.getBasketByUser(userDto);
        //then
        assertEquals(1L,returnedDto.getBasketId());
        assertEquals("login",returnedDto.getUser().getLogin());
    }

    @Test
    void getBasketByUserNotFound() {
        //given
        User user = new User();
        user.setLogin("login");
        Basket basket = new Basket();
        basket.setUser(user);
        UserDto userDto = UserDto.builder().login("login").build();
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto returnedDto = basketService.getBasketByUser(userDto);
        //then
        assertEquals("login",returnedDto.getUser().getLogin());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void createBasket() {
        //given
        User user = new User();
        user.setLogin("login");
        UserDto userDto = UserDto.builder().login("login").build();
        BasketDto basketDto = BasketDto.builder().basketId(2L).user(userDto).build();
        Basket basket = new Basket();
        basket.setBasketId(2L);
        basket.setUser(user);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto createdDto = basketService.createBasket(basketDto);
        //then
        assertEquals(2L,createdDto.getBasketId());
        assertEquals("login",createdDto.getUser().getLogin());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void updateBasketById() {
        User user = new User();
        user.setLogin("login");
        UserDto userDto = UserDto.builder().login("login").build();
        BasketDto basketDto = BasketDto.builder().basketId(2L).user(userDto).build();
        Basket basket = new Basket();
        basket.setBasketId(2L);
        basket.setUser(user);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto updatedDto = basketService.updateBasket(2L,basketDto);
        //then
        assertEquals(2L,updatedDto.getBasketId());
        assertEquals("login",updatedDto.getUser().getLogin());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void UpdateBasketByUser() {
        User user = new User();
        user.setLogin("login");
        UserDto userDto = UserDto.builder().login("login").build();
        BasketDto basketDto = BasketDto.builder().basketId(2L).user(userDto).build();
        Basket basket = new Basket();
        basket.setBasketId(2L);
        basket.setUser(user);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto updatedDto = basketService.updateBasket(userDto,basketDto);
        //then
        assertEquals(2L,updatedDto.getBasketId());
        assertEquals("login",updatedDto.getUser().getLogin());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void deleteBasket() {
        //when
        basketService.deleteBasket(3L);
        //then
        verify(basketRepository,times(1)).deleteByBasketId(any());
    }

    @Test
    void deleteBasketByUser() {
        basketService.deleteBasketByUser(UserDto.builder().build());
        //then
        verify(basketRepository,times(1)).deleteByUser(any());
    }
}
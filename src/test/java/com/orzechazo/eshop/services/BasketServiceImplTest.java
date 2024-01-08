package com.orzechazo.eshop.services;

import com.orzechazo.eshop.domain.Basket;
import com.orzechazo.eshop.domain.dto.BasketDto;
import com.orzechazo.eshop.exceptions.BadRequestException;
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
    void createBasket() {
        //given
        BasketDto basketDto = BasketDto.builder().build();
        Basket basket = new Basket();
        basket.setBasketId(2L);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto createdDto = basketService.createBasket(basketDto);
        //then
        assertEquals(2L,createdDto.getBasketId());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void createBasketNullId() {
        BasketDto basketDto = BasketDto.builder().basketId(2L).build();
        Basket basket = new Basket();
        basket.setBasketId(2L);

        Exception exception = assertThrows(BadRequestException.class,
                () -> basketService.createBasket(basketDto));

        assertEquals("Basket already has an id: 2",exception.getMessage());
    }

    @Test
    void updateBasketById() {
        BasketDto basketDto = BasketDto.builder().basketId(2L).build();
        Basket basket = new Basket();
        basket.setBasketId(2L);
        when(basketRepository.save(any())).thenReturn(basket);
        //when
        BasketDto updatedDto = basketService.updateBasket(basketDto);
        //then
        assertEquals(2L,updatedDto.getBasketId());
        verify(basketRepository,times(1)).save(any());
    }

    @Test
    void deleteBasket() {
        //when
        basketService.deleteBasket(3L);
        //then
        verify(basketRepository,times(1)).deleteByBasketId(any());
    }

}